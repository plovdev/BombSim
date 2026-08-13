package com.plovdev.bombsim.controls;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.plovdev.bombsim.events.EventManager;
import com.plovdev.bombsim.events.impls.BombTextChangedEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BombTextControl extends AbstractControl {
    private final BitmapText text;
    private BombTextControl textControl;
    private final Spatial display;
    private final Node displayNode = new Node("DisplayNode");
    private Node bombModel;
    private final List<Character> characters = new CopyOnWriteArrayList<>();
    private int visibleChars = 10;

    private int start = 0;
    private int end = visibleChars;
    private int tick = 1;

    private TextCorrector corrector = s -> s;


    public BombTextControl(Node bomb, AssetManager assetManager, InputManager inputManager) {
        bombModel = bomb;
        display = bombModel.getChild("Display");

        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        font.setRightToLeft(false);
        text = new BitmapText(font);
        text.setSize(0.3f);
        text.setQueueBucket(RenderQueue.Bucket.Transparent);
        text.setColor(ColorRGBA.Black);

        displayNode.attachChild(text);
        Vector3f textPos = display.getLocalTranslation();
        displayNode.setLocalTranslation(calculateTextPosition());
        bombModel.attachChild(displayNode);

        //Example mapping(for test)
        inputManager.addMapping("NextText", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("PrevText", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Clear", new KeyTrigger(KeyInput.KEY_C));
        inputManager.addMapping("Delete", new KeyTrigger(KeyInput.KEY_DELETE));
        inputManager.addMapping("Reverse", new KeyTrigger(KeyInput.KEY_R));

        inputManager.addListener((ActionListener) (name, pressed, tpf) -> {
            if (pressed) {
                if (name.equals("NextText")) {
                    next();
                } else {
                    previos();
                }

                switch (name) {
                    case "Clear" -> clear();
                    case "Reverse" -> reverse();
                    case "Delete" -> delete();
                }
            }
        }, "NextText", "PrevText", "Clear", "Reverse", "Delete");

        EventManager.getInstance().subscribe(new BombTextChangedEventListener(e -> {
            if (e instanceof String changed) {
                addText(changed);
            }
        }));
    }

    public void updateController(String text) {
        addText(text);
    }

    private Vector3f calculateTextPosition() {
        Vector3f textPos = display.getLocalTranslation();
        float x = textPos.x - 0.825f;
        float y = textPos.y + 0.97f;
        float z = textPos.z + 0.724f;
        return new Vector3f(x, y, z);
    }

    public BitmapText getText() {
        return text;
    }

    public BombTextControl getTextControl() {
        return textControl;
    }

    public void setTextControl(BombTextControl textControl) {
        this.textControl = textControl;
    }

    public Spatial getDisplay() {
        return display;
    }

    public Node getDisplayNode() {
        return displayNode;
    }

    public Node getBombModel() {
        return bombModel;
    }

    public void setBombModel(Node bombModel) {
        this.bombModel = bombModel;
    }

    public TextCorrector getCorrector() {
        return corrector;
    }

    public void setCorrector(TextCorrector corrector) {
        this.corrector = corrector;
        updateBombText();
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public int getVisibleChars() {
        return visibleChars;
    }

    public void setVisibleChars(int visibleChars) {
        this.visibleChars = visibleChars;
        updateBombText();
    }

    public synchronized void addText(String text) {
        for (char ch : text.toCharArray()) {
            characters.add(ch);
        }
        updateBombText();
        next();
    }

    public String getChars() {
        return corrector.correct(buildText(characters));
    }

    public synchronized void removeText(String text) {
        for (char ch : text.toCharArray()) {
            characters.remove(ch);
        }
        updateBombText();
    }
    public void clear() {
        characters.clear();
        updateBombText();
    }

    public void updateBombText() {
        updateBombText(corrector);
    }

    public synchronized void updateBombText(TextCorrector corrector) {
        List<Character> chars = characters.subList(0, Math.min(characters.size(), visibleChars));
        String buildedText = corrector.correct(buildText(chars));
        text.setText(buildedText);
    }

    private String buildText(List<Character> chars) {
        StringBuilder builder = new StringBuilder();
        chars.forEach(builder::append);
        return builder.toString();
    }

    public void paginateText() {
        paginateText(start, end);
    }
    public synchronized void paginateText(int start, int end) {
        int size = characters.size();
        List<Character> chars = characters.subList(start, Math.min(size, end));
        if (chars.size() < visibleChars) return;
        String buildedText = corrector.correct(buildText(chars));
        text.setText(buildedText);
    }

    public void next() {
        if (characters.size() <= visibleChars) return;

        setStart(Math.min(characters.size(), getStart() + tick));
        setEnd(getEnd() + tick);
        paginateText();
    }
    public void previos() {
        if (characters.size() <= visibleChars) return;

        setStart(Math.max(0, getStart() - tick));
        setEnd(Math.max(visibleChars, (getEnd() - tick)));
        paginateText();
    }
    public synchronized void reverse() {
        List<Character> chars = new ArrayList<>(characters.reversed());
        characters.clear();
        characters.addAll(chars);
        updateBombText();
    }

    public synchronized void delete() {
        if (characters.isEmpty()) return;
        characters.removeLast();
        updateBombText();
    }

    @Override
    protected void controlUpdate(float v) {

    }

    @Override
    protected void controlRender(RenderManager renderManager, ViewPort viewPort) {

    }
}