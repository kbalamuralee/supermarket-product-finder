# supermarket-product-finder
A simple program to find your way through a supermarket.

The layout of the supermarket is as follows.

The area is divided into cells (as in a grid), and along each vertical gridline is a shelf. Each horizontal gridline corresponds to a compartment inside that particular shelf. The number of shelves and compartments is a constant in the application that may be changed.

The inventory is read through a configuration file, 'inventory.properties'. Each product can be configured to be present on either or both of the side(s) of a given compartment on a shelf.

The program assumes that the user cannot move through the shelves.
