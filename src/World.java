
public class World {

    public static Room buildWorld() {
        Room office = new Room("Office", "Its the office.");
        Room living = new Room("Living Room",
                "I'm in the living room");
        Room bedroom = new Room("Bedroom",
                "My bedroom. I could really use a nap.");
        Room attic = new Room("Attic",
                "It's so dark and the air is disgusting.");
        Room kitchen = new Room("Kitchen",
                "I wish we had an airfryer.");
        Room garage = new Room("Garage",
                "The garage really needs to be cleaned out.");
        
        office.setLocked(true);
        attic.setLocked(true);
        garage.setLocked(true);
        
        
        office.addExit(living, 0);
        living.addExit(office, 1);
        living.addExit(bedroom, 2);
        living.addExit(kitchen, 0);
        bedroom.addExit(living, 3);
        bedroom.addExit(attic, 4);
        attic.addExit(bedroom, 5);
        kitchen.addExit(living, 1);
        kitchen.addExit(garage, 3);
        garage.addExit(kitchen, 2);

		bedroom.addItem(new OfficeKey());
		bedroom.addItem(new Item("bed",true));
		living.addItem(new Tool());
		living.addItem(new Item("musicbox"));
		living.addItem(new Item("couch", true));
		kitchen.addItem(new Item("flashlight"));
		kitchen.addItem(new Item("knife"));
		kitchen.addItem(new Item("orb"));
		attic.addItem(new Safe());

        return living;
    }

/*      public static void main(String[] args) {
        Room currentRoom = buildWorld();
        System.out.println(currentRoom);  // Living Room
        
        currentRoom = currentRoom.getExit(0);
        System.out.println(currentRoom);  // Kitchen
        
        currentRoom = currentRoom.getExit(3);
        System.out.println(currentRoom);  // Garage
        
        currentRoom = currentRoom.getExit(2);
        System.out.println(currentRoom);  // Kitchen Again
        
        currentRoom = currentRoom.getExit(1);
        System.out.println(currentRoom);  // Living Room Again
        
        currentRoom = currentRoom.getExit(2);
        System.out.println(currentRoom);  // Bedroom
        
        currentRoom = currentRoom.getExit(4);
        System.out.println(currentRoom);  // Attic
        
    }
 */     
      
      
      
}