package org.jetbrains.dummy.lang.exceptions

import org.jetbrains.dummy.lang.tree.FunctionCall

class WrongArgumentsException(val functionCall: FunctionCall) : Exception()