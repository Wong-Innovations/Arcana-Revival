package com.wonginnovations.arcana.network;

import com.wonginnovations.arcana.systems.research.Puzzle;
import com.wonginnovations.arcana.systems.research.ResearchBook;
import com.wonginnovations.arcana.systems.research.ResearchBooks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import static java.lang.String.format;

/**
 * Syncs all existing research. Not to be confused with {@link PkSyncPlayerResearch}, which syncs the player's progress.
 */
public class PkSyncResearch{
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	Map<ResourceLocation, ResearchBook> books;
	Map<ResourceLocation, Puzzle> puzzles;
	
	public PkSyncResearch(Map<ResourceLocation, ResearchBook> books, Map<ResourceLocation, Puzzle> puzzles) {
		this.books = books;
		this.puzzles = puzzles;
	}
	
	public static void encode(PkSyncResearch msg, FriendlyByteBuf buffer) {
		CompoundTag nbt = new CompoundTag();
		ListTag books = new ListTag(), puzzles = new ListTag();
		msg.books.forEach((location, book) -> books.add(book.serialize(location)));
		nbt.put("books", books);
		msg.puzzles.forEach((location, puzzle) -> puzzles.add(puzzle.getPassData()));
		nbt.put("puzzles", puzzles);
		buffer.writeNbt(nbt);
	}
	
	public static PkSyncResearch decode(FriendlyByteBuf buffer) {
		CompoundTag nbt = buffer.readNbt();
		PkSyncResearch msg = new PkSyncResearch(new LinkedHashMap<>(), new LinkedHashMap<>());
		if (nbt != null) {
			ListTag books = nbt.getList("books", 10);
			for (Tag bookElement : books) {
				CompoundTag book = (CompoundTag)bookElement;
				// deserialize book
				ResearchBook book1 = ResearchBook.deserialize(book);
				msg.books.put(book1.getKey(), book1);
			}
			ListTag puzzles = nbt.getList("puzzles", 10);
			for (Tag puzzleElement : puzzles) {
				CompoundTag puzzle = (CompoundTag)puzzleElement;
				// deserialize puzzle
				Puzzle puzzleObject = Puzzle.deserialize(puzzle);
				if (puzzleObject != null)
					msg.puzzles.put(puzzleObject.getKey(), puzzleObject);
				else
					LOGGER.error(format("An error occurred syncing research puzzles with client: could not deserialize Puzzle with type \"%s\"; invalid type.", puzzle.getString("type")));
			}
		} else
			LOGGER.error("An error occurred syncing research data with client: no or null NBT data was received.");
		return msg;
	}
	
	public static void handle(PkSyncResearch msg, Supplier<NetworkEvent.Context> supplier) {
		supplier.get().enqueueWork(() -> {
			ResearchBooks.books = msg.books;
			ResearchBooks.puzzles = msg.puzzles;
		});
		supplier.get().setPacketHandled(true);
	}
}