package org.jetbrains.dummy.lang.exceptions

import org.jetbrains.dummy.lang.tree.VariableDeclaration

class ConflictingDeclarationsException(val variableDeclaration: VariableDeclaration) : Exception()