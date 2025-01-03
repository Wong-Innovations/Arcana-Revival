package arcana.api.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

public class NBTIngredient extends Ingredient {

    private final CompoundTag tag;

    // Only meant to house a single item with NBT. add more recipes if you need a set of items with NBT.
    public NBTIngredient(ItemStack stack) {
        super(Stream.of(new ItemValue(stack)));
        this.tag = stack.getTag();
    }

    @Override
    public boolean test(ItemStack input) {
        boolean isSame = super.test(input);
        if (!isSame || tag == null) return isSame;
        if (input.getTag() == null) return false;
        CompoundTag inputTag = input.getTag();
        Set<String> inputKeys = input.getTag().getAllKeys();
        for (String key : tag.getAllKeys()) {
            if (!inputKeys.contains(key) || !tag.get(key).equals(inputTag.get(key))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    public static @NotNull Ingredient fromNetwork(FriendlyByteBuf pBuffer) {
        String nbtString = pBuffer.readUtf();
        String itemString = pBuffer.readUtf();
        CompoundTag ct = null;
        try {
            ct = TagParser.parseTag(nbtString);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemString));
        return new NBTIngredient(new ItemStack(item, 1, ct));
    }

    public void writeNetwork(FriendlyByteBuf pBuffer) {
        pBuffer.writeUtf(this.tag.toString());
        pBuffer.writeUtf(ForgeRegistries.ITEMS.getKey(this.getItems()[0].getItem()).toString());
    }

    public static @NotNull Ingredient fromJson(@Nullable JsonElement pJson, boolean pCanBeEmpty) {
        if (pJson != null && !pJson.isJsonNull()) {
            JsonObject obj = pJson.getAsJsonObject();
            try {
                CompoundTag ct = TagParser.parseTag(GsonHelper.getAsString(obj, "nbt"));
                if (obj.has("item")) {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(GsonHelper.getAsString(obj, "item")));
                    if (item == null) throw new JsonSyntaxException("Could not find item " + GsonHelper.getAsString(obj, "item"));
                    return new NBTIngredient(new ItemStack(item, 1, ct));
                } else {
                    throw new JsonSyntaxException("Could not parse Item");
                }
            } catch (CommandSyntaxException cse) {
                throw new JsonSyntaxException("NBT tag cannot be parsed");
            }
        } else {
            throw new JsonSyntaxException("Item cannot be null");
        }
    }

    @Override
    public @NotNull JsonElement toJson() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("nbt", this.tag.toString());
        jsonobject.addProperty("item", ForgeRegistries.ITEMS.getKey(this.getItems()[0].getItem()).toString());
        return jsonobject;
    }

}
