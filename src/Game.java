/* Tyler C Smith */

import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class Game {

	private static Room currentRoom;
	private static ArrayList<Item> inventory = new ArrayList<Item>();
	public static HashMap<String, String> roomDescriptions = new HashMap<String, String>();
	private static GameGUI gui;   // null when playing in the console

	private static final String SAVE_FILE = "save0.sav";
	
	private static void useItem(String name) {
	    for (Item item : inventory) {
	        if (item.getName().equals(name)) {
	            item.use();
	            return;
	        }
	    }
	    Game.print("You don't have a " + name + ".");
	}


	public static void main(String[] args) {
		loadDescriptions();
		currentRoom = World.buildWorld();
		if (args.length > 0 && args[0].equals("console")) {
			runGame();   // original text-only version: run with the argument "console"
			return;
		}
		javax.swing.SwingUtilities.invokeLater(() -> {
			gui = new GameGUI();
			gui.setVisible(true);
			print("Welcome to the adventure!");
			print("Type a command below, use the buttons, or click an item to take it.\n");
			updateGUI();
		});
	}
	
	public static void loadDescriptions() {
		try {
			Scanner read = new Scanner(new File("rooms.txt"));
			while (read.hasNextLine()) {
				String name = read.nextLine().trim();
//				if (name.isEmpty()) continue;          
//				if (!read.hasNextLine()) break;  
				String description = "";
				String line = read.nextLine().trim();
				while(!line.equals("#")) {
					description += line+ "\n";
					line = read.nextLine().trim();
				}
				roomDescriptions.put(name, description);
			}
			read.close();
		} catch (FileNotFoundException e) {
			Game.print("Could not find the descriptions file.");
		}
	}

	// ==================================================================
	// Lab 10: saving and loading
	// ==================================================================

	/** Writes the current room, the inventory, and every room to the save file. */
	private static void saveGame() {
		try {
			File saveFile = new File(SAVE_FILE);
			saveFile.createNewFile();
			ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(saveFile));
			outStream.writeObject(currentRoom);
			outStream.writeObject(inventory);
			outStream.writeObject(Room.rooms);
			outStream.close();
			Game.print("Game saved.");
			if (gui != null) {
				gui.flashStatus("Game saved.");
			}
		} catch (IOException e) {
			Game.print("Could not save the game: " + e.getMessage());
		}
	}

	/** Reads everything back in the SAME order saveGame wrote it. */
	@SuppressWarnings("unchecked")
	private static void loadGame() {
		try {
			File saveFile = new File(SAVE_FILE);
			ObjectInputStream inStream = new ObjectInputStream(new FileInputStream(saveFile));
			currentRoom = (Room) inStream.readObject();
			inventory = (ArrayList<Item>) inStream.readObject();
			Room.rooms = (ArrayList<Room>) inStream.readObject();
			inStream.close();
			Game.print("Game loaded.");
			if (gui != null) {
				gui.flashStatus("Game loaded.");
			}
		} catch (FileNotFoundException e) {
			Game.print("There is no saved game to load.");
		} catch (IOException | ClassNotFoundException e) {
			Game.print("Could not load the game: " + e.getMessage());
		}
	}

	// ==================================================================

	private static void move(String direction) {
		Room next;
		switch (direction) {
		case "east": next = currentRoom.getExit(0);
			break;
		case "west": next = currentRoom.getExit(1);
			break;
		case "north": next = currentRoom.getExit(2);
			break;
		case "south": next = currentRoom.getExit(3);
			break;
		case "up": next = currentRoom.getExit(4);
			break;
		case "down": next = currentRoom.getExit(5);
			break;
		default:
			Game.print("Not a valid direction of travel.");
			return;
		}
		if (next == null) {
			Game.print("You can't go that way.");
		} else if(next.isLocked()) {
			Game.print("The " + next.getName() + " is locked.");
		} else {
			currentRoom = next;
		}
	}

	private static void takeItem(String name) {
		Item item = currentRoom.getItem(name);
		if (item == null) {
			Game.print("There is no " + name + " to take.");
		} else if (item.isHeavy()) {
			Game.print("You can't lift the " + name + "!");
		} else {
			currentRoom.removeItem(name);
			inventory.add(item);
			Game.print("You take the " + item + " and carry it with you!");
		}
	}



	private static void showInventory() {
		if (inventory.isEmpty()) {
			Game.print("You are carrying nothing.");
		} else {
			Game.print("You are carrying:");
			for (Item item : inventory) {
				Game.print(item);
			}
		}
	}

	public static void runGame() {
		Scanner input = new Scanner(System.in);
		String command = "";
		

		while (!command.equals("exit")) {
			Game.print(currentRoom);

			Game.print("Enter a command(Enter exit to stop): ");
			String choice = input.nextLine().toLowerCase();
			
			choice = choice.replaceAll("\\b(the|to)\\b", "");
			choice = choice.replaceAll("\\s+", " ").trim();
			
			String[] words = choice.split(" ");
			command = words[0];

			switch (command) {
			case "east":
			case "west":
			case "north":
			case "south":
			case "up":
			case "down":
				move(command);
				break;
			case "go":
				if(words.length < 2) {
					Game.print("Go where?");
				}else {
					move(words[1]);
				}
				break;
			case "take":
				if (words.length < 2) {
					Game.print("Take what?");
				}else {
					takeItem(words[1]);
				}
				break;
			case "i":
				showInventory();
				break;
			case "save":
				saveGame();
				break;
			case "load":
				loadGame();
				break;
			case "exit":
				Game.print("Yeah, alright. I was bored playing with you too.");
				break;
			default:
				Game.print("Not a valid command.");
				break;
			}
		}
		input.close();
	}

	/**
	 * GUI version of runGame: no loop and no Scanner. The GUI calls this once
	 * for each command the player types (or each button they click).
	 */
	public static void runGameGUI(String choice) {
		Room roomBefore = currentRoom;

		choice = choice.toLowerCase();
		choice = choice.replaceAll("\\b(the|to)\\b", "");
		choice = choice.replaceAll("\\s+", " ").trim();

		String[] words = choice.split(" ");
		String command = words[0];

		switch (command) {
		case "east":
		case "west":
		case "north":
		case "south":
		case "up":
		case "down":
			move(command);
			break;
		case "go":
			if(words.length < 2) {
				Game.print("Go where?");
			}else {
				move(words[1]);
			}
			break;
		case "take":
			if (words.length < 2) {
				Game.print("Take what?");
			}else {
				takeItem(words[1]);
			}
			break;
		case "i":
			showInventory();
			break;
		case "save":
			saveGame();
			break;
		case "load":
			loadGame();   // currentRoom changes, so the room gets described below
			break;
		case "exit":
			Game.print("Yeah, alright. I was bored playing with you too.");
			gui.closeLater();
			break;
		case "use":
			if(words.length < 2) {
				Game.print("Use what?");
			}else {
				useItem(words[1]);
			}
			break;
		default:
			Game.print("Not a valid command.");
			break;
		}

		// Note the new room in the story log when the player moved (or loaded a save).
		// The full description shows in the "Where you are" panel via updateGUI().
		if (currentRoom != roomBefore) {
			gui.appendRoomEntry(currentRoom.getName());
		}
		updateGUI();
	}

	/** Refreshes the banner, room description, item panels, and direction buttons. */
	private static void updateGUI() {
		gui.setLocationName(currentRoom.getName());
		gui.setRoomDescription(currentRoom.getDescription());

		String items = "";
		for (Item item : inventory) {
			items += item + "\n";
		}
		gui.setInventoryText(items.trim());

		// Just the names: the player finds out an item is heavy when they try to take it
		ArrayList<String> here = new ArrayList<String>();
		for (Item item : currentRoom.getItems()) {
			here.add(item.getName());
		}
		gui.setRoomItems(here.toArray(new String[0]));

		// Same index order that move() uses: 0 east, 1 west, 2 north, 3 south, 4 up, 5 down
		String[] directionNames = {"east", "west", "north", "south", "up", "down"};
		ArrayList<String> open = new ArrayList<String>();
		for (int i = 0; i < directionNames.length; i++) {
			if (currentRoom.getExit(i) != null) {
				open.add(directionNames[i]);
			}
		}
		gui.setAvailableDirections(open.toArray(new String[0]));
	}

	public static void print(Object obj) {
		if (gui != null) {
			gui.appendText(String.valueOf(obj));
		} else {
			System.out.println(obj);
		}
	}

	public static Room getCurrentRoom() {
		return currentRoom;
	}
}


//	Game Name:  A Trip to Newfoundland
//	Map:		Home. The Woods. The Creek. The Cave. Newfoundland. Abandoned Town.
//	Goal:		To make it to Newfoundland alive
//	Obstacles:	pirate/bandits. Bear/Ogre. Traps. locals.