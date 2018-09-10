package Pokemon.game;

import engine.base.Size;
import engine.graphics.Sprite;
import engine.input.GenericInput;
import engine.input.InputBuffer;
import engine.main.MotionObject;
import engine.main.Stage;

import java.util.Arrays;

public class Player extends MotionObject {
    InputBuffer inputBuffer;

    public Player() {
        currentSprite = new Sprite(16, 32,"/images/red.png")
                .setOrigin(0, 8)
                .setFramesPerFrame(8)
                .animate(false);

        onAdd.addListener(stage -> bindControls(stage));
        onRemove.addListener(stage -> unbindControls(stage));

        inputBuffer = new InputBuffer(Arrays.asList(
                GenericInput.Button.Left,
                GenericInput.Button.Right,
                GenericInput.Button.Up,
                GenericInput.Button.Down
        ));

        size.set(16,16);
        checkForCollisions = true;

        onCollision.addListener(gameObject -> {

            if(gameObject instanceof Flower){

                ((Flower) gameObject ).explode();

            }

        });
    }

    protected void bindControls(Stage stage) {
        inputBuffer.bind(stage);
    }

    protected void unbindControls(Stage stage) {
        inputBuffer.unbind(stage);
    }

    protected void updateMovementState() {
        int movementSpeed = 100;
        int stateOffset = 0;

        if (currentStage.getGame().getInput().isPressed(GenericInput.Button.B)) {
            movementSpeed = 150;
            stateOffset = 4;
        }

        speed.set(0,0);

        Integer btn = inputBuffer.getInput();

        if (btn != null) {
            switch (btn) {
                case GenericInput.Button.Down:
                    speed.y = movementSpeed;
                    currentSprite.setState(stateOffset);
                    break;
                case GenericInput.Button.Up:
                    speed.y = -movementSpeed;
                    currentSprite.setState(2 + stateOffset);
                    break;
                case GenericInput.Button.Left:
                    speed.x = -movementSpeed;
                    currentSprite.setState(3 + stateOffset);
                    break;
                case GenericInput.Button.Right:
                    speed.x = movementSpeed;
                    currentSprite.setState(1 + stateOffset);
                    break;
            }
        }
    }

    @Override
    public void update(double secondsElapsed) {
        int grid = 16;

        if (Math.round(position.x) % grid == 0 && Math.round(position.y) % grid == 0) {
            updateMovementState();
        }


        if (speed.x == 0 && speed.y == 0) {
            position.x = Math.round(position.x);
            position.y = Math.round(position.y);

            currentSprite.setFrame(0);
            currentSprite.animate(false);
        } else {
            currentSprite.animate(true);
        }


        super.update(secondsElapsed);
    }
}
