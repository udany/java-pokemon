package engine.input;

import engine.main.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InputBuffer {
    List<Integer> allowedButtons;
    private List<Integer> buttonBuffer = new ArrayList<>();
    protected UUID id = UUID.randomUUID();

    public InputBuffer(List<Integer> allowedButtons) {
        this.allowedButtons = allowedButtons;
    }

    public InputBuffer bind(Stage stage) {
        stage.onButtonDown.addListener(btn -> {
            if (allowedButtons.contains(btn)) {
                buttonBuffer.add(btn);
            }
        }, id.toString());

        stage.onButtonUp.addListener(btn -> {
            if (allowedButtons.contains(btn)) {
                buttonBuffer.remove(btn);
            }
        }, id.toString());

        return this;
    }

    public InputBuffer unbind(Stage stage) {
        stage.onButtonDown.removeListener(this.id.toString());
        stage.onButtonUp.removeListener(this.id.toString());

        return this;
    }

    public Integer getInput() {
        if (buttonBuffer.size() > 0) {
            return buttonBuffer.get(buttonBuffer.size() - 1);
        }

        return null;
    }
}
