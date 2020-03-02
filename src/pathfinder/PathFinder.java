package pathfinder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PathFinder {

	private static final String PATH_INVENTORY_CONFIG = "pathfinder/inventory.properties";

	private static final String PROPERTY_SHELF_COUNT = "shelf_count";

	private static final String PROPERTY_COMPARTMENT_COUNT = "compart_count";

	private static final String SEPARATOR_LOCATION = ",";

	private static final String SEPARATOR_LOCATION_DETAILS = "_";

	private static final String DIRECTION_UP = "up";

	private static final String DIRECTION_DOWN = "down";

	private static final String DIRECTION_LEFT = "left";

	private static final String DIRECTION_RIGHT = "right";

	private static final String MESSAGE_AISLE_EXIT = "Please proceed %d compartment(s) %s and exit the aisle.";

	private static final String MESSAGE_AISLE_TRAVERSE = "Please move %d aisle(s) towards your %s.";

	private static final String MESSAGE_AISLE_ENTRY = "Please enter the aisle you are at right now and proceed %d compartment(s) %s.";

	private static final String MESSAGE_COMPARTMENT_TRAVERSE = "Please proceed %d compartment(s) %s to reach your destination.";

	private static final String MESSAGE_COMPARTMENT_REACHED = "You are now at the correct aisle and compartment.";

	private static final String MESSAGE_SUCCESS = "Please find the product on shelf #%d, compartment #%d.";

	private static final String MESSAGE_PRODUCT_NOT_FOUND = "Could not locate the product. Please try again.";

	private static final String MESSAGE_ARGUMENTS_ERROR = "Insufficient arguments. Please try again.";

	private static final String MESSAGE_LOCATION_ERROR = "Invalid location. Please try again.";

	private static final String MESSAGE_INVENTORY_READ_ERROR = "Error reading inventory. Please try again.";

	public static void main(String[] args) {

		if (args.length < 3) {
			System.err.println(MESSAGE_ARGUMENTS_ERROR);
			System.exit(-1);
		}

		try (InputStream inputStream = new FileInputStream(PATH_INVENTORY_CONFIG)) {

			Properties inventory = new Properties();
			inventory.load(inputStream);

			final int SHELF_COUNT = Integer.parseInt(inventory.getProperty(PROPERTY_SHELF_COUNT));
			final int COMPARTMENT_COUNT = Integer.parseInt(inventory.getProperty(PROPERTY_COMPARTMENT_COUNT));

			String productName = args[0];
			String locationString = inventory.getProperty(productName);

			if (locationString == null || locationString.equals("")) {
				System.err.println(MESSAGE_PRODUCT_NOT_FOUND);
				System.exit(-1);
			}

			int currentAisle = Integer.parseInt(args[1]);
			int currentCompartment = Integer.parseInt(args[2]);

			if (!((1 <= currentAisle && currentAisle <= SHELF_COUNT + 1)
					&& (0 <= currentCompartment && currentCompartment <= COMPARTMENT_COUNT + 1))) {
				System.err.println(MESSAGE_LOCATION_ERROR);
				System.exit(-1);
			}

			Integer closestProductDistance = null;
			Integer closestProductShelf = null;
			Integer closestProductCompartment = null;
			Boolean closestProductOnLeftOfShelf = null;

			String[] locations = locationString.split(SEPARATOR_LOCATION);

			for (int i = 0; i < locations.length; i++) {

				String[] locationDetails = locations[i].split(SEPARATOR_LOCATION_DETAILS);

				int productShelf = Integer.parseInt(locationDetails[0]);
				int productCompartment = Integer.parseInt(locationDetails[1]);

				String productSide = locationDetails[2];
				Boolean productOnLeftOfShelf = null;

				if (productSide.equals(DIRECTION_LEFT)) {
					productOnLeftOfShelf = true;
				}

				else if (productSide.equals(DIRECTION_RIGHT)) {
					productOnLeftOfShelf = false;
				}

				else {
					System.err.println(MESSAGE_INVENTORY_READ_ERROR);
					System.exit(-1);
				}

				int distance = getDistance(currentAisle, currentCompartment, productShelf, productCompartment,
						productOnLeftOfShelf, COMPARTMENT_COUNT);

				if (closestProductDistance == null || (distance < closestProductDistance)) {
					closestProductDistance = distance;
					closestProductShelf = productShelf;
					closestProductCompartment = productCompartment;
					closestProductOnLeftOfShelf = productOnLeftOfShelf;
				}

			}

			if (closestProductDistance == null) {
				System.out.println(MESSAGE_PRODUCT_NOT_FOUND);
				System.exit(-1);
			}

			List<String> instructions = getInstructionsToReachCompartment(currentAisle, currentCompartment,
					closestProductShelf, closestProductCompartment, closestProductOnLeftOfShelf, COMPARTMENT_COUNT);

			for (int i = 0; i < instructions.size(); i++) {
				System.out.println(instructions.get(i));
			}

			System.exit(0);

		} catch (IOException e) {

			e.printStackTrace();

			System.err.println(MESSAGE_INVENTORY_READ_ERROR);
			System.exit(-1);

		}

	}

	public static int getDistance(int currentAisle, int currentCompartment, int productShelf, int productCompartment,
			boolean productOnLeftOfShelf, final int COMPARTMENT_COUNT) {

		int distance = 0;

		int productAisle = productOnLeftOfShelf ? productShelf : (productShelf + 1);

		if (productAisle != currentAisle && ((0 < currentCompartment) && (currentCompartment < COMPARTMENT_COUNT))) {

			boolean approachDownwards = ((currentCompartment + productCompartment) <= (COMPARTMENT_COUNT + 1));

			int exitDistance = approachDownwards ? currentCompartment : (COMPARTMENT_COUNT - currentCompartment);
			distance += exitDistance;

			boolean productTowardsLeft = productAisle < currentAisle;

			int travelDistance = productTowardsLeft ? (currentAisle - productAisle) : (productAisle - currentAisle);
			distance += travelDistance;

			int entryDistance = approachDownwards ? productCompartment : (COMPARTMENT_COUNT - productCompartment);
			distance += entryDistance;

		}

		else if (productAisle != currentAisle
				&& !((0 < currentCompartment) && (currentCompartment < COMPARTMENT_COUNT))) {

			boolean productTowardsLeft = productAisle < currentAisle;

			int travelDistance = productTowardsLeft ? (currentAisle - productAisle) : (productAisle - currentAisle);
			distance += travelDistance;

			boolean approachDownwards = (currentCompartment > productCompartment);

			int entryDistance = approachDownwards ? (currentCompartment - productCompartment) : productCompartment;
			distance += entryDistance;

		}

		else if (productAisle == currentAisle
				&& !((0 < currentCompartment) && (currentCompartment < COMPARTMENT_COUNT))) {

			boolean approachDownwards = (currentCompartment > productCompartment);

			int entryDistance = approachDownwards ? (currentCompartment - productCompartment) : productCompartment;
			distance += entryDistance;

		}

		else if (productCompartment != currentCompartment) {

			boolean productDownwards = (currentCompartment > productCompartment);

			int travelDistance = productDownwards ? (currentCompartment - productCompartment)
					: (productCompartment - currentCompartment);
			distance += travelDistance;

		}

		return distance;
	}

	public static List<String> getInstructionsToReachCompartment(int currentAisle, int currentCompartment,
			int productShelf, int productCompartment, boolean productOnLeftOfShelf, final int COMPARTMENT_COUNT) {

		List<String> instructions = new ArrayList<>();

		int productAisle = productOnLeftOfShelf ? productShelf : (productShelf + 1);

		if (productAisle != currentAisle
				&& ((0 < currentCompartment) && (currentCompartment < COMPARTMENT_COUNT + 1))) {

			boolean approachDownwards = ((currentCompartment + productCompartment) <= (COMPARTMENT_COUNT + 1));

			String exitDirection = approachDownwards ? DIRECTION_DOWN : DIRECTION_UP;
			int exitDistance = approachDownwards ? currentCompartment : ((COMPARTMENT_COUNT + 1) - currentCompartment);

			instructions.add(String.format(MESSAGE_AISLE_EXIT, exitDistance, exitDirection));

			boolean productTowardsLeft = productAisle < currentAisle;

			String travelDirection = productTowardsLeft ? DIRECTION_LEFT : DIRECTION_RIGHT;
			int travelDistance = productTowardsLeft ? (currentAisle - productAisle) : (productAisle - currentAisle);

			instructions.add(String.format(MESSAGE_AISLE_TRAVERSE, travelDistance, travelDirection));

			String entryDirection = approachDownwards ? DIRECTION_UP : DIRECTION_DOWN;
			int entryDistance = approachDownwards ? productCompartment : ((COMPARTMENT_COUNT + 1) - productCompartment);

			instructions.add(String.format(MESSAGE_AISLE_ENTRY, entryDistance, entryDirection));

		}

		else if (productAisle != currentAisle
				&& !((0 < currentCompartment) && (currentCompartment < COMPARTMENT_COUNT + 1))) {

			boolean productTowardsLeft = (productAisle < currentAisle);

			String travelDirection = productTowardsLeft ? DIRECTION_LEFT : DIRECTION_RIGHT;
			int travelDistance = productTowardsLeft ? (currentAisle - productAisle) : (productAisle - currentAisle);

			instructions.add(String.format(MESSAGE_AISLE_TRAVERSE, travelDistance, travelDirection));

			boolean approachDownwards = (currentCompartment > productCompartment);

			String entryDirection = approachDownwards ? DIRECTION_DOWN : DIRECTION_UP;
			int entryDistance = approachDownwards ? (currentCompartment - productCompartment) : productCompartment;

			instructions.add(String.format(MESSAGE_AISLE_ENTRY, entryDistance, entryDirection));

		}

		else if (productAisle == currentAisle
				&& !((0 < currentCompartment) && (currentCompartment < COMPARTMENT_COUNT + 1))) {

			boolean approachDownwards = (currentCompartment > productCompartment);

			String entryDirection = approachDownwards ? DIRECTION_DOWN : DIRECTION_UP;
			int entryDistance = approachDownwards ? (currentCompartment - productCompartment) : productCompartment;

			instructions.add(String.format(MESSAGE_AISLE_ENTRY, entryDistance, entryDirection));

		}

		else if (productCompartment != currentCompartment) {

			boolean productDownwards = ((currentCompartment - productCompartment) > 0);

			String travelDirection = productDownwards ? DIRECTION_DOWN : DIRECTION_UP;
			int travelDistance = productDownwards ? (currentCompartment - productCompartment)
					: (productCompartment - currentCompartment);

			instructions.add(String.format(MESSAGE_COMPARTMENT_TRAVERSE, travelDistance, travelDirection));

		}

		instructions.add(MESSAGE_COMPARTMENT_REACHED);
		instructions.add(String.format(MESSAGE_SUCCESS, productShelf, productCompartment));

		return instructions;
	}

}
