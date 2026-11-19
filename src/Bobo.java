/* Tyler C Smith */

public class Bobo extends NPC {
	private static final long serialVersionUID = 1L;
	private int convoNumber;

	public Bobo() {
		super("bobo");   // name must be lower-case
	}

	@Override
	public void talk() {
		switch (convoNumber) {
		case 0:
			convoZero();
			break;
		case 1:
			convoOne();
			break;
		case 2:
			convoTwo();
			break;
		case 3:
			convoThree();
			break;
		case 4:   // from convoOne, option 1 ("I love pineapple")
			say("Oh, it's you again. The pineapple guy. Please leave.");
			break;
		case 5:   // from convoOne, option 2 ("...ew")
			say("My guy! Pineapple haters must stick together.");
			break;
		case 6:   // from convoTwo, option 1 (Spanish)
			say("Gracias de nuevo amigo.");
			break;
		case 7:   // from convoTwo, option 2 (Peaches)
			say("I am Peaches! Kneel before me, worm!");
			break;
		case 8:   // from convoTwo, option 3 (beyond help)
			say("I told my therapist about you.. jerk..");
			break;
		case 9:   // from convoThree, option 1
			say("Gumball guy is back. Nope. Still no 50 cents.");
			break;
		case 10:  // from convoThree, option 2
			say("Mr. Mysterious... I'm watching you.");
			break;
		}
		
	}
	
	public void convoZero() {
		say("Hello! How may I help you today?");
		String[] responses = {
			"Got any gum?",
			"You can't help me. I'm here to help you.",
			"Can I have 50 cents?"
		};
		int option = getResponse(responses);
		switch(option) {
		case 1:
			say("All I have left is pineapple-flavored gum.");
			convoNumber = 1;
			break;
		case 2:
			say("Ooh! Nobody's ever helped me before. Thanks!");
			convoNumber = 2;
			break;
		case 3:
			say("No.");
			convoNumber = 3;
			break;
		default:
			say("Uhhh…what?");
		}
	}
	
	public void convoOne() {
		say("It's kind of old too.");
		String[] r1 = {
				"Oh yay! I love pineapple!",
				"...ew"
		};
		int option = getResponse(r1);
		switch(option) {
		case 1:
			say("You're one fry short of a Happy Meal ain't ya.");
			convoNumber = 4;
			break;
		case 2:
			say("thats facts frfr");
			convoNumber = 5;
			break;
		default:
			say("..uh ..sorry?");
			
		}
	}
	
	public void convoTwo() {
		say("Are you going to help me?");
		String[] r1 = {
				"I'm going to teach you Spanish: Me llamo Bobo.",
				"I'm giving you a new name: Peaches.",
				"No. I was kidding before. You're beyond help."
		};
		int option = getResponse(r1);
		switch(option) {
		case 1:
			say("Me llamo Bobo. Yay! I speak Spanish now!");
			convoNumber = 6;
			break;
		case 2:
			say("Ooooh! I feel fuzzy…");
			setName("peaches");
			convoNumber = 7;
			break;
		case 3:
			say("What is your problem??");
			convoNumber = 8;
			break;
		default:
			say("Thanks for nothing.");
		}
	}
	
	public void convoThree() {
		say("Why do you even need 50 cents? Be honest.");
		String[] r1 = {
				"For a gumball.",
				"None of your business."
		};
		int option = getResponse(r1);
		switch(option) {
		case 1:
			say("A Gumball?? Are you 5?");
			convoNumber = 9;
			break;
		case 2:
			say("Okay, Mr. Mysterious. Still no.");
			convoNumber = 10;
			break;
		default:
			say("lol ur broke");
		}
	}
}
