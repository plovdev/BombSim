package com.plovdev.bombsim.controls.gui;

import com.plovdev.bombsim.dto.Author;
import com.plovdev.bombsim.utils.AuthorsLoader;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.controls.ScrollPanel;
import de.lessvoid.nifty.elements.Element;

import java.util.Objects;

public class AuthorsScreenControl extends BaseScreenController {
    public void back() {
        nifty.gotoScreen("menu");
    }

    @Override
    public void onStartScreen() {
        Element container = screen.findElementById("profileContainer");
        if (container == null) return;
        container.getChildren().forEach(Element::markForRemoval);

        for (Author author : AuthorsLoader.loadAllAuthors()) {
            String avatarPath = author.avatar();
            String name = author.name();
            String info = author.bio();
            String cardId = author.id();

            PanelBuilder cardBuilder = new PanelBuilder(cardId) {{
                childLayout(ChildLayoutType.Horizontal);
                width("100%");
                height("80px");
                marginBottom("60px");
                backgroundColor("#333f");
                padding("7px");

                image(new ImageBuilder() {{
                    filename(avatarPath);
                    width("70px");
                    height("70px");
                    valignCenter();
                }});

                panel(new PanelBuilder() {{
                    childLayout(ChildLayoutType.Vertical);
                    width("100%");
                    height("100%");
                    paddingLeft("15px");
                    valignCenter();

                    text(new TextBuilder() {{
                        text(name);
                        font("aurulent-sans-16.fnt");
                        color("#ffffffff");
                        alignLeft();
                    }});

                    text(new TextBuilder() {{
                        text(info);
                        font("aurulent-sans-16.fnt");
                        color("#aaaaaaff");
                        alignLeft();
                        marginTop("5px");
                    }});
                }});
            }};

            //noinspection deprecation
            cardBuilder.build(nifty, screen, container);
        }

        ScrollPanel scrollPanel = screen.findNiftyControl("profileScrollPanel", ScrollPanel.class);
        if (scrollPanel != null) {
            Objects.requireNonNull(scrollPanel.getElement()).layoutElements();
        }
    }

    @Override
    public void onEndScreen() {
    }
}