import java.util.*;

import static java.util.Map.entry;

public class Bj {
    public static void main(String[] args) {
        System.out.println("Welcome to a game of BlackJack");
        Game newGame = new Game();
        Scanner scanner = new Scanner(System.in);
        if (newGame.getDealerValue() == 10) {
            newGame.generateDealerSecondCard(false);
        } else if (newGame.getDealerValue() == 11) {
            String insurance = "";
            while (!insurance.equals("y") && !insurance.equals("n")) {
                System.out.print("Pay insurance? (y/n): ");
                insurance = scanner.nextLine();
                if (insurance.equals("y")) {
                    newGame.generateDealerSecondCard(true);
                } else if (insurance.equals("n")) {
                    newGame.generateDealerSecondCard(false);
                }
            }
        } else if (newGame.getUserValue() == 21) {
            System.out.println("You won!");
            newGame.gameEnded = true;
        }
        String move = "";
        while (!move.equals("y") && !move.equals("n") && !newGame.gameEnded) {
            if (newGame.doubleAvailable) {
                System.out.print("Hit, stand, or double? (h/s/d): ");
            } else {
                System.out.print("Hit or stand? (h/s): ");
            }
            move = scanner.nextLine();
            if (move.equals("h")) {
                newGame.hit();
                if (newGame.getUserValue() != 21) {
                    newGame.showCards();
                }
                if (newGame.getUserValue() > 21) {
                    System.out.println("Bust");
                }
            } else if (move.equals("s")) {
                newGame.stand();
            } else if (move.equals("d") && newGame.doubleAvailable) {
                newGame.doubleAvailable = false;
                newGame.hit();
                if (newGame.getUserValue() != 21) {
                    newGame.showCards();
                }
                if (newGame.getUserValue() > 21) {
                    System.out.println("Bust");
                } else {
                    newGame.stand();
                }
            }
        }
        scanner.close();
    }
}

class Game {
    private final List<String> dealerCards = new ArrayList<>();
    private final List<String> userCards = new ArrayList<>();
    private int userValue;
    private int dealerValue;

    private int userAce = 0;
    private int dealerAce = 0;

    private List<Object> dealerSecondCard = new ArrayList<>();

    boolean doubleAvailable = true;

    boolean gameEnded = false;

    public Game() {
        List<Object> userFirstCard = generateCard();
        List<Object> userSecondCard = generateCard();
        List<Object> dealerFirstCard = generateCard();
        userValue = (int) userFirstCard.get(1) + (int) userSecondCard.get(1);
        if (userValue == 22) {
            userValue = 12;
            userAce--;
        }
        if ((int) userFirstCard.get(1) == 11) userAce++;
        if ((int) userSecondCard.get(1) == 11) userAce++;
        dealerValue = (int) dealerFirstCard.get(1);
        if ((int) dealerFirstCard.get(1) == 11) dealerAce++;
        userCards.add((String) userFirstCard.get(0));
        userCards.add((String) userSecondCard.get(0));
        dealerCards.add((String) dealerFirstCard.get(0));
        showCards();
    }

    public int getUserValue() {
        return userValue;
    }

    public int getDealerValue() {
        return dealerValue;
    }

    public String getUserCards() {
        return userCards.toString();
    }

    public String getDealerCards() {
        return dealerCards.toString();
    }

    public void showCards() {
        System.out.println("Your cards = " + getUserCards());
        System.out.println("Dealer cards = " + getDealerCards());
        System.out.println("Your Total = " + getUserValue());
        System.out.println("Dealer Total = " + getDealerValue());
        System.out.println();

    }


    public void hit() {
        List<Object> userNewCard = generateCard();
        userValue += (int) userNewCard.get(1);
        userCards.add((String) userNewCard.get(0));
        if ((int) userNewCard.get(1) == 11) userAce++;
        if (userValue > 21) {
            if (userAce > 0) {
                userValue -= 10;
                userAce--;
            } else {
                gameEnded = true;
            }
        }
        if (userValue == 21) {
            gameEnded = true;
            stand();
        }
    }

    private static List<Object> generateCard() {
        Map<Integer, List<Object>> cards = Map.ofEntries(entry(1, List.of("Ace", 11)), entry(2, List.of("2", 2)), entry(3, List.of("3", 3)), entry(4, List.of("4", 4)), entry(5, List.of("5", 5)), entry(6, List.of("6", 6)), entry(7, List.of("7", 7)), entry(8, List.of("8", 8)), entry(9, List.of("9", 9)), entry(10, List.of("10", 10)), entry(11, List.of("J", 10)), entry(12, List.of("Q", 10)), entry(13, List.of("K", 10)));
        Map<Integer, String> symbols = Map.ofEntries(entry(1, "♣"), entry(2, "♥"), entry(3, "♦"), entry(4, "♠"));
        Random random = new Random();
        int number = random.nextInt(13) + 1;
        List<Object> values = cards.get(number);
        String symbol = symbols.get(random.nextInt(4) + 1);
        //It returns the Name and the value
        return List.of(values.get(0) + symbol, values.get(1));
    }

    public void stand() {
        gameEnded = true;
        if (dealerSecondCard.isEmpty()) {
            dealerSecondCard = generateCard();
        }
        dealerValue += (int) dealerSecondCard.get(1);
        if (dealerValue == 22) {
            dealerValue = 12;
            dealerAce--;
        }
        dealerCards.add((String) dealerSecondCard.get(0));
        if ((int) dealerSecondCard.get(1) == 11) dealerAce++;
        while (dealerValue < 17) {
            List<Object> dealerNewCard = generateCard();
            dealerValue += (int) dealerNewCard.get(1);
            if ((int) dealerNewCard.get(1) == 11) dealerAce++;
            if (dealerValue > 21 && dealerAce > 0) {
                dealerValue -= 10;
                dealerAce--;
            }
            dealerCards.add((String) dealerNewCard.get(0));
        }
        showCards();
        if (dealerValue > 21) {
            System.out.println("Dealer Bust");
        } else {
            if (dealerValue < userValue) {
                System.out.println("You win");
            } else if (dealerValue > userValue) {
                System.out.println("Dealer won");
            } else {
                System.out.println("Draw");
            }
        }
    }


    public void generateDealerSecondCard(boolean insurance) {
        dealerSecondCard = generateCard();
        if (dealerValue == 10) {
            if ((int) dealerSecondCard.get(1) == 11) {
                dealerValue += 11;
                dealerCards.add((String) dealerSecondCard.get(0));
                gameEnded = true;
                showCards();
                if (userValue == 21) {
                    System.out.println("Draw");
                } else {
                    System.out.println("Dealer has BlackJack");
                }
            }
        } else if (dealerValue == 11) {
            if ((int) dealerSecondCard.get(1) == 10) {
                dealerValue += 10;
                gameEnded = true;
                dealerCards.add((String) dealerSecondCard.get(0));
                showCards();
                if (insurance) {
                    System.out.println("You've got the money back from the insurance");
                }
                if (userValue == 21) {
                    System.out.println("Draw");
                } else {
                    System.out.println("Dealer has BlackJack");
                }
            } else {
                if (insurance) {
                    System.out.println("You've lose the insurance. Dealer doesn't have BlackJack");
                } else {
                    System.out.println("Dealer Doesn't have BlackJack");
                }
                if(userValue == 21) {
                    System.out.println("You have BlackJack");
                }
            }
        }
    }

}