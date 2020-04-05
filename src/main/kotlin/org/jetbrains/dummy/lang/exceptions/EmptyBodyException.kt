package org.jetbrains.dummy.lang.exceptions

import org.jetbrains.dummy.lang.tree.Block

class EmptyBodyException(val block : Block) : Exception()