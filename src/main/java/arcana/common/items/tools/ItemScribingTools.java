package arcana.common.items.tools;

import arcana.api.items.IScribeTools;
import arcana.common.items.ItemBase;

public class ItemScribingTools extends ItemBase implements IScribeTools {

    public ItemScribingTools() {
        this(new Properties());
    }

    public ItemScribingTools(Properties pProperties) {
        super(pProperties);
    }
}
