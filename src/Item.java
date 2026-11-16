import java.io.Serializable;

public class Item implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private boolean heavy;

	public Item(String name) {
		this(name, false);
	}
	
	public Item(String name,boolean heavy) {
		this.name = name;
		this.heavy = heavy;
	}
	
	public boolean isHeavy() {
		return heavy;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}
	
	public void use() {
		Game.print("You can't use the " + getName() + ".");
	}

	// Lab 12
	public void open() {
		Game.print("You can't open the " + getName() + ".");
	}

	public void close() {
		Game.print("You can't close the " + getName() + ".");
	}

	public void read() {
		Game.print("You can't read the " + getName() + ".");
	}
}