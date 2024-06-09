package com.debugdesk.mono.utils

import java.text.DecimalFormat
import kotlin.math.pow

class CalculateMathExpression {
    private val df = DecimalFormat("#.###")

    companion object {
        private val calculateMathExpression = CalculateMathExpression()
        fun String.calculate() = with(calculateMathExpression) {
            df.format(evaluate(this@calculate) ?: 0.0)
                .takeIf {
                    (evaluate(this@calculate) ?: 0.0) - (evaluate(this@calculate)
                        ?: 0.0).toInt() != 0.0
                } ?: (evaluate(
                this@calculate
            ) ?: 0.0).toInt()
        }
    }

    private fun basic(rightNum: String?, leftNum: String?, op: String?): Double {
        return when (op) {
            "+" -> {
                ((rightNum?.toDouble() ?: 0.0) + (leftNum?.toDouble() ?: 0.0))
            }

            "-" -> {
                ((rightNum?.toDouble() ?: 0.0) - (leftNum?.toDouble() ?: 0.0))
            }

            "*" -> {
                ((rightNum?.toDouble() ?: 0.0) * (leftNum?.toDouble() ?: 0.0))
            }

            "^" -> {
                ((rightNum?.toDouble() ?: 0.0).pow(leftNum?.toDouble() ?: 0.0))
            }

            else -> {
                ((rightNum?.toDouble() ?: 0.0) / (leftNum?.toDouble() ?: 0.0))
            }
        }
    }

    private fun elemInside(mainString: String?, listCheck: List<String>): Boolean {
        for (ops in listCheck) {
            if (mainString?.contains(ops)!!) {
                return true
            }
        }
        return false
    }

    private fun getOpIndex(query: String?, operations: List<String>): Array<Int> {
        var allIndex: Array<Int> = arrayOf()
        var dupQuery = query
        while (elemInside(dupQuery, operations)) {
            for (op in operations) {
                if (dupQuery?.contains(op)!!) {
                    allIndex = allIndex.plusElement(dupQuery.indexOf(op))
                    dupQuery = dupQuery.substring(
                        0,
                        dupQuery.indexOf(op)
                    ) + '1' + dupQuery.substring(dupQuery.indexOf(op) + 1)
                }
            }
        }

        allIndex.sort()
        return allIndex
    }

    private fun parseSimple(query: String?): Double {
        val operations = listOf("^", "/", "*", "-", "+")
        var allIndex: Array<Int> = arrayOf()

        var calcQuery = query
        while (elemInside(
                calcQuery,
                operations
            ) && (allIndex.size > 1 || if (allIndex.isEmpty()) true else allIndex[0] != 0)
        ) {
            for (op in operations) {
                calcQuery = calcQuery?.replace("-+", "-")
                calcQuery = calcQuery?.replace("--", "+")
                calcQuery = calcQuery?.replace("+-", "-")
                allIndex = getOpIndex(calcQuery, operations)
                if (calcQuery?.contains(op)!!) {
                    val indexOp = calcQuery.indexOf(op)
                    val indexIndexOp = allIndex.indexOf(indexOp)
                    val rightIndex =
                        if (indexIndexOp == allIndex.lastIndex) calcQuery.lastIndex else allIndex[indexIndexOp + 1]
                    val leftIndex = if (indexIndexOp == 0) 0 else allIndex[indexIndexOp - 1]
                    val rightNum =
                        calcQuery.slice(if (rightIndex == calcQuery.lastIndex) indexOp + 1..rightIndex else indexOp + 1 until rightIndex)
                    val leftNum =
                        calcQuery.slice(if (leftIndex == 0) leftIndex until indexOp else leftIndex + 1 until indexOp)
                    val result = basic(leftNum, rightNum, op)
                    calcQuery = (if (leftIndex != 0) calcQuery.substring(
                        0,
                        leftIndex + 1
                    ) else "") + result.toString() + (if (rightIndex != calcQuery.lastIndex) calcQuery.substring(
                        rightIndex..calcQuery.lastIndex
                    ) else "")
                }
            }
        }
        return calcQuery?.toDouble() ?: 0.0
    }

    private fun getAllIndex(query: String?, char: Char, replacement: String = "%"): List<Int> {
        var myQuery = query
        var indexes: List<Int> = listOf()
        while (char in myQuery!!) {
            val indexFinded = myQuery.indexOf(char)
            indexes = indexes.plus(indexFinded)
            myQuery =
                myQuery.substring(0 until indexFinded) + replacement + myQuery.substring(indexFinded + 1..myQuery.lastIndex)
        }
        return indexes
    }

    private fun getBrackets(query: String?): List<Int> {
        val allEndIndex = getAllIndex(query, ')')
        val allStartIndex = getAllIndex(query, '(')
        val firstIndex = allStartIndex[0]
        for (endIndex in allEndIndex) {
            val inBrac = query?.substring(firstIndex + 1 until endIndex)
            val inBracStart = getAllIndex(inBrac, '(')
            val inBracEnd = getAllIndex(inBrac, ')')
            if (inBracStart.size == inBracEnd.size) {
                return listOf(firstIndex, endIndex)
            }
        }
        return listOf(-1, -1)
    }

    private fun evaluate(query: String?): Double {
        var calcQuery = query
        var index = 0
        // Check if brackets are present
        while (calcQuery?.contains('(')!! && index < 200) {
            val startBrackets = getBrackets(calcQuery)[0]
            val endBrackets = getBrackets(calcQuery)[1]
            val inBrackets = calcQuery.slice(startBrackets + 1 until endBrackets)
            calcQuery = if ('(' in inBrackets && ')' in inBrackets) {
                val inBracValue = evaluate(inBrackets)
                calcQuery.substring(
                    0,
                    startBrackets
                ) + inBracValue.toString() + (if (endBrackets == calcQuery.lastIndex) "" else calcQuery.substring(
                    endBrackets + 1..calcQuery.lastIndex
                ))
            } else {
                val inBracValue = parseSimple(inBrackets)
                calcQuery.substring(
                    0,
                    startBrackets
                ) + inBracValue.toString() + (if (endBrackets == calcQuery.lastIndex) "" else calcQuery.substring(
                    endBrackets + 1..calcQuery.lastIndex
                ))
            }
            index++
        }

        return parseSimple(calcQuery) ?: 0.0
    }
}

enum class CalculatorEnum {
    Cancel,
    Okay
}