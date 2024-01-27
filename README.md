# DeltaDebugging Alloy

# Implementation of Automated Delta Debugging Algorithm to find MUS on Alloy Models

## Overview
This repository contains the Java implementation of the Automated Delta Debugging Algorithm, as described in the academic paper titled "[Yesterday, My Program Worked. Today, It Does Not, Why?]". This algorithm focuses on isolating the minimal failure-inducing input in a Alloy model.

## Authors of the Paper
- [Andreas Zeller]

## Key Concepts
The paper presents an innovative approach to debugging, which involves:
- Minimizing the input data set that causes a test to fail.
- Recursive strategies for isolating the source of faults.

## Implementation Details
Implemented in Java, this project faithfully represents the algorithms as outlined in the paper. It includes:
- Recursive partitioning of input data.
- Handling of unresolved test cases in the extended algorithm.

### Directory Structure

* [src](./src)
  * [test](./src/test)
    * [DDPlusTest.java](./src/test/DDPlusTest.java)
  * [alloy](./src/alloy)
    * [IDDPlusTest.java](./src/alloy/IDDPlusTest.java)
    * [AlloyManager.java](./src/alloy/AlloyManager.java)
  * [dto](./src/dto)
    * [DataTransportObject.java](./src/dto/DataTransportObject.java)
  * [ddmin](./src/ddmin)
  * [AbstractDDPlus.java](./src/ddmin/AbstractDDPlus.java)
