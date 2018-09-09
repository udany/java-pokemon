package engine.main;

import engine.base.Vector;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class QuadTree {
    private int MAX_OBJECTS = 4;
    private int MAX_LEVELS = 12;

    private int level;
    private List<GameObject> objects;
    private float posX, posY, width, height;
    private QuadTree[] nodes;

    private QuadTree parent;
    HashMap<String, QuadTree> nodeIndex;

    /**
     * Ideal constructor for making a quadtree that's empty, simply calls the normal constructor with
     * this(0, 0, 0, width, height)
     * @param width your stage width in units
     * @param height your stage height in units
     */
    public QuadTree(float width, float height){
        this(0, 0, 0, width, height, null);
    }

    /**
     *
     * @param pLevel start at level 0 if you're creating an empty tree
     * @param x x coordinate
     * @param y y coordinate
     * @param w width
     * @param h height
     */
    public QuadTree(int pLevel, float x, float y, float w, float h) {
        this(pLevel, x, y, w, h, null);
    }

    public QuadTree(int pLevel, float x, float y, float w, float h, QuadTree p) {
        level = pLevel;
        objects = new ArrayList<>();
        posX = x;
        posY = y;
        width = w;
        height = h;
        parent = p;

        nodes = new QuadTree[4];

        if (isRoot()) {
            nodeIndex = new HashMap<>();
        } else {
            setMaxLevels(parent.getMaxLevels());
            setMaxObjects(parent.getMaxObjects());
            nodeIndex = parent.nodeIndex;
        }
    }

    public boolean isRoot() {
        return parent == null;
    }


    public int getMaxObjects() {
        return MAX_OBJECTS;
    }
    public QuadTree setMaxObjects(int o) {
        MAX_OBJECTS = o;
        return this;
    }

    public int getMaxLevels() {
        return MAX_LEVELS;
    }
    public QuadTree setMaxLevels(int l) {
        MAX_LEVELS = l;
        return this;
    }


    /*
     * Clears the quadtree
     */
    public void clear() {
        objects.clear();

        for (QuadTree node : nodes) {
            if (node != null) {
                node.clear();
            }
        }
        nodes = new QuadTree[4];
    }

    /*
     * Splits the node into 4 sub-nodes
     */
    private void split() {
        float subWidth = (width / 2);
        float subHeight = (height / 2);
        float x = posX;
        float y = posY;

        nodes[0] = new QuadTree(level + 1, x + subWidth, y, subWidth, subHeight, this);
        nodes[1] = new QuadTree(level + 1, x, y, subWidth, subHeight, this);
        nodes[2] = new QuadTree(level + 1, x, y + subHeight, subWidth, subHeight, this);
        nodes[3] = new QuadTree(level + 1, x + subWidth, y + subHeight, subWidth, subHeight, this);

        // Gets rid of all of it's objects that can fit within a sub-node
        int i = 0;
        while (i < objects.size()) {
            int index = getIndex(objects.get(i));
            if (index != -1) {
                nodes[index].insert(objects.remove(i));
            } else {
                i++;
            }
        }
    }

    /*
     * Determine which node the object belongs to. -1 means object cannot
     * completely fit within a child node and is part of the parent node
     */
    private int getIndex(GameObject object) {
        int index = -1;
        double verticalMidpoint = posX + (width / 2);
        double horizontalMidpoint = posY + (height / 2);

        // Object can completely fit within the top quadrants
        boolean topQuadrant = (object.getY() < horizontalMidpoint && object.getY()
                + object.getHeight() < horizontalMidpoint);
        // Object can completely fit within the bottom quadrants
        boolean bottomQuadrant = (object.getY() > horizontalMidpoint);

        // Object can completely fit within the left quadrants
        if (object.getX() < verticalMidpoint && object.getX() + object.getWidth() < verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 2;
            }
        }
        // Object can completely fit within the right quadrants
        else if (object.getX() > verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            } else if (bottomQuadrant) {
                index = 3;
            }
        }

        return index;
    }

    /*
     * Insert the object into the quadtree. If the node exceeds the capacity, it
     * will split and add all objects to their corresponding nodes.
     */
    public QuadTree insert(GameObject obj) {
        if (isRoot()) {
            remove(obj);
        }

        if (objects.size() >= MAX_OBJECTS && level < MAX_LEVELS) {
            if (nodes[0] == null) {
                split();
            }
        }

        if (nodes[0] != null) {
            int index = getIndex(obj);

            if (index != -1) {
                return nodes[index].insert(obj);
            } else {
                return insertIntoList(obj);
            }
        } else {
            return insertIntoList(obj);
        }
    }
    public void insertAll(List<GameObject> objs) {
        for(GameObject obj : objs) {
            insert(obj);
        }
    }
    public QuadTree insertIntoList(GameObject obj) {
        if (!objects.contains(obj)) {
            objects.add(obj);
            addToIndex(obj, this);
        }

        return this;
    }

    public void remove(GameObject obj) {
        if (isRoot()) {
            QuadTree owner = nodeIndex.get(obj.getId());
            if (owner != null) {
                owner.removeFromSelf(obj);
            }

            nodeIndex.remove(obj.getId());
        } else {
            parent.remove(obj);
        }
    }
    public void removeAll(List<GameObject> objs) {
        for(GameObject obj : objs) {
            remove(obj);
        }
    }

    public void prune() {
        List<GameObject> objListTemp = new ArrayList<>();

        for (GameObject obj : objects) {
            if (obj.isDestroyed()) {
                objListTemp.add(obj);
            }
        }
        objects.removeAll(objListTemp);
        removeAll(objListTemp);

        int emptyNodes = 0;

        for (QuadTree node : nodes) {
            if (node != null) {
                node.prune();
                if (node.getObjectCount() == 0) {
                    emptyNodes ++;
                }
            }
        }

        if (emptyNodes == 4) {
            nodes = new QuadTree[4];
        }
    }

    public int getObjectCount() {
        int count = 0;

        for (QuadTree node : nodes) {
            if (node != null) {
                count += node.getObjectCount();
            }
        }

        count += objects.size();

        return count;
    }

    private void removeFromSelf(GameObject obj) {
        objects.remove(obj);
    }

    public void update(GameObject obj) {
        insert(obj);
    }

    private void addToIndex(GameObject obj, QuadTree node){
        nodeIndex.put(obj.getId(), node);
    }

    public boolean intersects(Vector position) {
        return (position.x >= posX && position.x <= (posX+width)) && (position.y >= posY && position.y <= (posY+height));
    }
    public boolean intersects(GameObject obj) {
        Vector tempPos = obj.position.clone();

        return intersects(tempPos) || // TopLeft
                intersects(tempPos.add(obj.size)) || //BottomRight
                intersects(tempPos.subtract(obj.size.width, 0)) || // BottomLeft
                intersects(tempPos.subtract(-obj.size.width, obj.size.height)); //TopRight
    }

    /*
     * Return all objects that could collide with the given object
     */
    public List<GameObject> retrieve(GameObject area) {
        return retrieve(area, null);
    }

    public List<GameObject> retrieve(GameObject area, List<GameObject> returnObjects) {
        if (returnObjects == null) returnObjects = new ArrayList<>();

        for (QuadTree node : nodes) {
            if (node != null && node.intersects(area)) {
                node.retrieve(area, returnObjects);
            }
        }

        returnObjects.addAll(objects);

        return returnObjects;
    }


    public void draw(Graphics2D graphics) {
        Rectangle box = new Rectangle((int) posX, (int) posY, (int) width, (int) height);
        graphics.setColor(new Color(255, 0,0, 105));
        graphics.draw(box);
        graphics.setColor(new Color(255, 53, 159, 32));
        graphics.fill(box);


        Vector center = new Vector(posX, posY).add(width/2, height/2);

        graphics.setFont( new Font( "Courier New", Font.PLAIN, 10 ) );
        graphics.setColor(new Color(251, 227, 255, 113));

        graphics.drawString(objects.size()+"", (int) center.x-15, (int) center.y-15);

        for (QuadTree node : nodes) {
            if (node != null) {
                node.draw(graphics);
            }
        }
    }
}
