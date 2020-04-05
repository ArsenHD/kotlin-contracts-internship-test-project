package org.jetbrains.dummy.lang.exceptions

import org.jetbrains.dummy.lang.tree.VariableAccess

class AccessBeforeInitializationException(val access: VariableAccess) : Exception()
