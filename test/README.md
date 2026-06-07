# JUnit 5 Tests

## Overview
This directory contains JUnit 5 (Jupiter) unit tests for the Honor of Kings IMS project.
Tests are organized by package mirroring the source structure.

## Test Classes

| Class | Tests | Covers |
|-------|-------|--------|
| `PersonTest` | 5 | Abstract class, inheritance, encapsulation, polymorphism |
| `PlayerTest` | 7 | Composition (hero ownership), defensive copies, Reportable interface |
| `TeamTest` | 8 | Aggregation (bidirectional refs), computed stats, empty team safety |
| `GameDataManagerTest` | 8 | Cascade delete (4 entity types), dataset minimums, defensive copies, duplicate ID rejection |
| `RankingServiceTest` | 8 | Equipment formula sorting, tie-breaking, custom score, formatting |

**Total: 36 unit tests across 5 classes**

## How to Run

### Option 1: With JUnit 5 console launcher (recommended)

1. Download `junit-platform-console-standalone-1.10.x.jar` from https://search.maven.org/search?q=g:org.junit.platform
2. Compile tests:
   ```bash
   javac -encoding UTF-8 -d out -cp out;junit-platform-console-standalone-1.10.2.jar -sourcepath test;src test/hok/**/*.java
   ```
3. Run tests:
   ```bash
   java -jar junit-platform-console-standalone-1.10.2.jar -cp out --select-package hok
   ```

### Option 2: With Gradle/Maven

Add JUnit 5 dependency and run:
```bash
gradle test
```
or
```bash
mvn test
```

## Test Results Summary

| Metrics | Value |
|---------|-------|
| Total Tests | 36 |
| Test Classes | 5 |
| OOP Concepts Verified | 10 (inheritance, interface, polymorphism, encapsulation, aggregation, composition, association, collections, exception handling, enums) |
