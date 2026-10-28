import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Room implements Serializable {
	private static final long serialVersionUID = 1L;

	// Lab 10: every Room ever created is kept here so the game can save them all.
	// It's static, so there is only one list shared by the whole program.
	public static ArrayList<Room> rooms = new ArrayList<Room>();

	private String name;
//	private String description;
	private Room[] exits;
//	private Item item;   // each room holds at most one item (for now)
	private HashMap<String, Item> items;

	public Room(String name, String description) {
		this.name = name;
//		this.description = description;
		this.exits = new Room[6];
		this.items = new HashMap<>();
		rooms.add(this);   // Lab 10: remember this room
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return Game.roomDescriptions.get(name);
//		return description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		Game.roomDescriptions.put(name,  description);
//		this.description = description;
	}

	public String toString() {
		return name + "\n" + Game.roomDescriptions.get(name);
//		return name + "\n" + description;
	}

	public void addExit(Room room, int direction) {
		exits[direction] = room;
	}

	public Room getExit(int direction) {
		return exits[direction];
	}

	public Item getItem(String itemName) {
		return items.get(itemName);
	}

	public void addItem(Item item) {
		items.put(item.getName(), item);
	}

	public Item removeItem(String itemName) {
		return items.remove(itemName);
	}

	/** Returns every item in this room (sorted by name), so the game can show what is here. */
	public ArrayList<Item> getItems() {
		ArrayList<Item> list = new ArrayList<Item>(items.values());
		Collections.sort(list, (a, b) -> a.getName().compareTo(b.getName()));
		return list;
	}
}