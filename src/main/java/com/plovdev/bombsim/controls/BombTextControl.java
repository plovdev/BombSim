package com.plovdev.bombsim.controls;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import org.jspecify.annotations.NonNull;

public class BombTextControl extends AbstractControl {
    private final BitmapText text;
    private Spatial display;
    private final Node displayNode = new Node("DisplayNode");
    private final StringBuilder characters = new StringBuilder();

    private Node model;
    private BombTextControl textControl;

    private int visibleChars = 10;
    private int start = 0;
    private int end = visibleChars;
    private int tick = 1;

    public BombTextControl(@NonNull AssetManager assetManager, @NonNull InputManager inputManager) {
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        font.setRightToLeft(false);
        text = new BitmapText(font);
        text.setSize(0.3f);
        text.setQueueBucket(RenderQueue.Bucket.Transparent);
        text.setColor(ColorRGBA.Black);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        model = (Node) spatial;
        display = model.getChild("Display");
        model.attachChild(displayNode);

        displayNode.attachChild(text);
        displayNode.setLocalTranslation(calculateTextPosition());
    }

    public void updateController(String text) {
        addText(text);
    }

    private @NonNull Vector3f calculateTextPosition() {
        Vector3f textPos = display.getLocalTranslation();
        float x = textPos.x - 0.825f;
        float y = textPos.y + 0.97f;
        float z = textPos.z + 0.724f;
        return new Vector3f(x, y, z);
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

    public void addText(@NonNull String text) {
        characters.append(text);
        updateBombText();
        next();
    }

    public String getChars() {
        return characters.toString();
    }

    public void removeText(int s, int e) {
        characters.delete(s, e);
        updateBombText();
    }

    public void clear() {
        characters.setLength(0);
        updateBombText();
    }

    public synchronized void updateBombText() {
        String buildedText = characters.substring(0, Math.min(characters.length(), visibleChars));
        text.setText(buildedText);
    }

    public void paginateText() {
        paginateText(start, end);
    }

    public synchronized void paginateText(int start, int end) {
        int size = characters.length();
        String chars = characters.substring(start, Math.min(size, end));
        if (chars.length() < visibleChars) return;
        text.setText(chars);
    }

    public void next() {
        int chLength = characters.length();
        if (chLength <= visibleChars) return;

        setStart(Math.min(chLength, getStart() + tick));
        setEnd(getEnd() + tick);
        paginateText();
    }

    public void previos() {
        if (characters.length() <= visibleChars) return;

        setStart(Math.max(0, getStart() - tick));
        setEnd(Math.max(visibleChars, (getEnd() - tick)));
        paginateText();
    }

    public synchronized void reverse() {
        characters.reverse();
        updateBombText();
    }

    public synchronized void delete() {
        if (characters.isEmpty()) return;
        characters.deleteCharAt(characters.length() - 1);
        updateBombText();
    }

    @Override
    protected void controlUpdate(float v) {
    }

    @Override
    protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
    }
}