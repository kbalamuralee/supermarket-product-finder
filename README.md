# supermarket-product-finder

A simple program to find your way through a supermarket.

Compile and run from 'src' directory with the arguments as according to the following.

java PathFinder <product-name> <current-aisle-number> <current-compartment-number>

The layout of the supermarket is as follows.

The area is divided into cells (as in a grid), and along each vertical gridline is a shelf. Each horizontal gridline corresponds to a compartment inside that particular shelf. The number of shelves and compartments are parameters in the application that may be changed.

The aisles are the areas between the vertical gridlines, and the user can pick from any of the shelves, on their left or right, from each aisle. There is thus one more aisle than the number of shelves.

Compartments are numbered from 0 to (MAX-COMPARTMENT-COUNT-PER-SHELF + 1). The actual compartments corresponding to the shelves are numbered from 1 to (MAX-COMPARTMENT-COUNT-PER-SHELF), and the remaining end compartments are representative of the free area at the borders of the store. The user can move through the aisles, and these free areas only.

The shelf/compartment counts and the inventory are read through a configuration file, 'inventory.properties'. Each product can be configured to be present on either or both of the side(s) of any given compartment on any shelf.
