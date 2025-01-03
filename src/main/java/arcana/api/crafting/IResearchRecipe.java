package arcana.api.crafting;

import org.jetbrains.annotations.NotNull;

public interface IResearchRecipe {
    String getResearch();
    default @NotNull String getGroup() {
        return "";
    }
}
