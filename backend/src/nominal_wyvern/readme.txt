Nominal Wyvern Bytecode Implementation

Experimental Wyvern backend (bytecode compiler) based on Nominal Wyvern.

To support all existing Wyvern functionality, Nominal Wyvern bytecode
is implemented as a standalone language for now. In order to integrate
the new backend into the existing Wyvern compiler, must either
  1) implement the necessary features of the existing bytecode in nw-bytecode
    and support them in the backend, or
  2) implement a translation into Nominal Wyvern.
(likely to end up with a combination of the two)

Bytecode spec: backend/nw-bytecode.proto
Implementation: backend/src/nominal_wyvern

To run the bytecode compiler, run the following in the backend/src directory
$ ../../bin/wyvern nominal_wyvern/main.wyv