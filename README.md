# Synonym-Network-Explorer
A graph-based tool for exploring semantic relationships between words through their synonym networks.

## Overview

The Synonym Network Explorer analyzes and traverses relationships between words using a graph structure where:
- Vertices represent individual words
- Edges connect words to their synonyms
- Definitions are stored in an on-demand symbol table

## Core Features

### 1. Semantic Distance Calculator
Determines the degree of relationship between any two words by analyzing their connections in the thesaurus network. This helps understand how closely related different terms are based on their shared semantic space.

### 2. Random Word Generator
- Takes a source word and depth parameter as input
- Traverses the synonym network to the specified depth
- Returns a random word from that depth level
- Displays definitions for both the source and generated words

## Technical Implementation

- **Graph Structure**: Implements a directed graph representing the thesaurus network
- **Lazy Loading**: Definitions are stored in a symbol table and retrieved only when needed, optimizing memory usage
- **Path Finding**: Uses graph traversal algorithms to determine word relationships