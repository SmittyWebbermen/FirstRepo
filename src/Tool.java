

public class Tool extends Item {
	private static final long serialVersionUID = 1L;
    private boolean usedTool;

    public Tool() {
        super("tool");
        usedTool = false;
    }

    public void use() {
        if (Game.getCurrentRoom().getName().equals("Bedroom")) {
            if (usedTool) {
                Game.print("You already opened the attic door!");
            } else {
                Game.getCurrentRoom().getExit(4).setLocked(false);
                usedTool = true;
                Game.print("Using the tool, you hook the door in the ceiling and pull it open. "
                         + "A ladder drops down to the attic.");
            }
        } else {
            Game.print("You don't see any way to use the tool here.");
        }
    }
}