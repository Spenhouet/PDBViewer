package views.toggle;

import views.graph.styles.*;

public enum StyleType {
    BALL_AND_STICK(BallAndStickStyle.class),
    SPACE_FILLING(SpaceFillingStyle.class),
    BACKBONE(BackboneStyle.class),
    RIBBON(RibbonStyle.class),
    CARTOON(CartoonStyle.class);

    private final Class<? extends Style> styleClass;

    <T extends Style> StyleType(Class<T> styleClass) {
        this.styleClass = styleClass;
    }

    public Class<? extends Style> getStyleClass() {
        return styleClass;
    }
}
