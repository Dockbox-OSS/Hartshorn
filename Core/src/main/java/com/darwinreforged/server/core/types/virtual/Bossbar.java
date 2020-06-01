package com.darwinreforged.server.core.types.virtual;

import com.darwinreforged.server.core.chat.Text;

public class Bossbar {

    private String id;
    private BaseColor color;
    private Text title;
    private float percent = -1;

    public BaseColor getColor() {
        return color;
    }

    public Text getTitle() {
        return title;
    }

    public float getPercent() {
        return percent;
    }

    public void setColor(BaseColor color) {
        this.color = color;
    }

    public void setTitle(Text title) {
        this.title = title;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static BossbarBuilder builder() {
        return new BossbarBuilder();
    }

    public static final class BossbarBuilder {
        private BaseColor color;
        private Text title;
        private float percent;
        private String id;

        private BossbarBuilder() {
        }

        public BossbarBuilder id(String id) {
            this.id = id;
            return this;
        }

        public BossbarBuilder color(BaseColor color) {
            this.color = color;
            return this;
        }

        public BossbarBuilder title(Text title) {
            this.title = title;
            return this;
        }

        public BossbarBuilder percent(float percent) {
            this.percent = percent;
            return this;
        }

        public BossbarBuilder but() {
            return builder().color(color).title(title).percent(percent);
        }

        public Bossbar build() {
            Bossbar bossbar = new Bossbar();
            bossbar.id = this.id;
            bossbar.color = this.color;
            bossbar.title = this.title;
            bossbar.percent = this.percent;
            return bossbar;
        }
    }
}
