package main.compression;

import java.util.*;
import java.io.ByteArrayOutputStream; // Optional

/**
 * Huffman instances provide reusable Huffman Encoding Maps for
 * compressing and decompressing text corpi with comparable
 * distributions of characters.
 */
public class Huffman {
    
    // -----------------------------------------------
    // Construction
    // -----------------------------------------------

    private HuffNode trieRoot;
    // TreeMap chosen here just to make debugging easier
    private TreeMap<Character, String> encodingMap;
    // Character that represents the end of a compressed transmission
    private static final char ETB_CHAR = 23;
    
    /**
     * Creates the Huffman Trie and Encoding Map using the character
     * distributions in the given text corpus
     * 
     * @param corpus A String representing a message / document corpus
     *        with distributions over characters that are implicitly used
     *        throughout the methods that follow. Note: this corpus ONLY
     *        establishes the Encoding Map; later compressed corpi may
     *        differ.
     */
    // >> [TN] Notice how bloated with code your constructor got? When so much is happening
    // it's hard to see the constituent pieces, debug, and interpret. This would've been the
    // perfect opportunity to decompose into helper methods that each took a part of the job:
    // (0.5 off for each that there wasn't a helper made for): (a) Finding the character distribution,
    // (b) building the Huffman Trie, (c) constructing the encoding map. (-.5)
    public Huffman (String corpus) {
        // TODO!
        
    	/* New hashmap to store frequency of characters in corpus and then
    	 sets initial frequency of the ETB_CHAR character to 1 */
    	Map<Character, Integer> frequency = new HashMap<>(); frequency.put(ETB_CHAR, 1);
    	
    	// for loop to iterate through each character in corpus
    	for (int index = 0; index < corpus.length(); index++) {
    		
    		// increment by 1 if it's already in the frequency map
    		if (frequency.containsKey(corpus.charAt(index))) {
    			frequency.put(corpus.charAt(index), frequency.get(corpus.charAt(index)) + 1);
    		}
    		
    		// Add character to the map if it's not already
    		else {
    			frequency.put(corpus.charAt(index), 1); 
    		}	
    	}
    	
    	// new priority queue of huffnode for the nodes
    	PriorityQueue<HuffNode> nodes = addQueue(frequency);
    	
    	// add nodes to priority queue, new treemap to store encoding map, traverse trie and assign binary
    	this.addTrie(nodes);
    	this.encodingMap = new TreeMap<>();
    	this.addMap(this.trieRoot, "");
    }
    
    
    // -----------------------------------------------
    // Compression
    // -----------------------------------------------
    
    /**
     * Compresses the given String message / text corpus into its Huffman coded
     * bitstring, as represented by an array of bytes. Uses the encodingMap
     * field generated during construction for this purpose.
     * 
     * @param message String representing the corpus to compress.
     * @return {@code byte[]} representing the compressed corpus with the
     *         Huffman coded bytecode. Formatted as:
     *         (1) the bitstring containing the message itself, (2) possible
     *         0-padding on the final byte.
     */
    public byte[] compress (String message) {
        // TODO!
        
    	// Initialize empty string to store bits
    	String empty = "";
    	
    	// Loop to go through each character in the message
        for (int index = 0; index < message.length(); index++) {
        	
        	// Get the corresponding encoding from the encoding map
        	char currentChar = message.charAt(index);
        	String encoding = this.encodingMap.get(currentChar);
        	
        	// Add the string to encoding
        	empty += encoding;
        }
        
        // Add empty to map and return it using the change method
        empty += this.encodingMap.get(ETB_CHAR);
        return changeBytes(empty);  
        
    }
    
    
    // -----------------------------------------------
    // Decompression
    // -----------------------------------------------
    
    /**
     * Decompresses the given compressed array of bytes into their original,
     * String representation. Uses the trieRoot field (the Huffman Trie) that
     * generated the compressed message during decoding.
     * 
     * @param compressedMsg {@code byte[]} representing the compressed corpus with the
     *        Huffman coded bytecode. Formatted as:
     *        (1) the bitstring containing the message itself, (2) possible
     *        0-padding on the final byte.
     * @return Decompressed String representation of the compressed bytecode message.
     */
    public String decompress (byte[] compressedMsg) {
        // TODO!
    	
    	// Initialize variables to store decoded characters and current position in trie
        String decodedCharacters = "";
        HuffNode current = this.trieRoot;

        // Convert byte array to binary string
        String compressedString = "";
        for (int j = 0; j < compressedMsg.length; j++) {
            
        	// Convert each byte to binary representation and pad with leading zeros
            String compressedByteString = Integer.toBinaryString(compressedMsg[j] & 0xff);
            while (compressedByteString.length() % 8 != 0) {
                compressedByteString = "0" + compressedByteString;
            }
            
            // Concatenate the binary string to the compressedString
            compressedString += compressedByteString;
        }

        // Traverse trie using the compressedString to decode the characters
        for (int index = 0; index < compressedString.length(); index++) {
            if (current.isLeaf()) {
                
            	// If current node is a leaf node, check if its ETB
                if (current.character == ETB_CHAR) {
                    
                	// Return the decoded characters
                    return decodedCharacters;
                } 
                else {
                    
                	// Append character to decodedCharacters and reset node
                    decodedCharacters += current.character;
                    current = this.trieRoot;
                    index--;
                }
            } 
            else {
                
            	// If node is not leaf node, traverse child node based on compressedString
                if (compressedString.charAt(index) == '0') {
                    current = current.zeroChild;
                } 
                else {
                    current = current.oneChild;
                }
            }
        }

        // Return decoded characters
        return decodedCharacters;
    }
    
    
    // -----------------------------------------------
    // Huffman Trie
    // -----------------------------------------------
    
    /**
     * Huffman Trie Node class used in construction of the Huffman Trie.
     * Each node is a binary (having at most a left (0) and right (1) child), contains
     * a character field that it represents, and a count field that holds the 
     * number of times the node's character (or those in its subtrees) appear 
     * in the corpus.
     */
    private static class HuffNode implements Comparable<HuffNode> {
        
        HuffNode zeroChild, oneChild;
        char character;
        int count;
        
        HuffNode (char character, int count) {
            this.count = count;
            this.character = character;
        }
        
        public boolean isLeaf () {
            return this.zeroChild == null && this.oneChild == null;
        }
        
        public int compareTo (HuffNode other) {
            // TODO: 

        	// Compare count values of both nodes
        	if(this.count == other.count) {
        		
        		// Subtract nodes
        		return this.character - other.character;
        	}
        	
        	// Return nodes
        	return this.count - other.count;
        }
        
    }
    
    // HELPER METHODS
    // >> [TN] Provide proper Javadocs for ALL methods, including helpers you write (-1)
    private void addTrie (PriorityQueue<HuffNode> queue) {
    	
    	// While the queue's size is greater then 1
    	while (queue.size() > 1) {
    		
    		// Poll two nodes with lowest counters
    		HuffNode nodePop1 = queue.poll(); 
    		HuffNode nodePop2 = queue.poll();
    		
    		// Create new parent node with character
            // >> [TN] Ah whoops -- here's a problem: remember that nodes are prioritized by frequency first
            // but then with ties broken by their character field. What happens if you give all non-leaves
            // the same character? Review the tiebreaking criteria, which is earliest character *in a subtree*
    		HuffNode parent = new HuffNode(nodePop1.character, nodePop1.count + nodePop2.count);
    		
    		// Add the parent node back to priority queue
    		queue.add(parent);
    		
    		// Set the zero and one child of parent node
    		parent.zeroChild = nodePop1;
    		parent.oneChild = nodePop2;	
    	}
    	
    	// Set root of huffman trie as last
    	this.trieRoot = queue.poll();	
    }
    
    private void addMap (HuffNode node, String bitString) {
    	
    	// if node is leaf
    	if (node.isLeaf()) {
    		
    		// Add character and huffman to encoding map
    		this.encodingMap.put(node.character, bitString);
    		return;
    	}
    	else {
    		
    		// If node isn't leaf, recurse using traversals to add encoding
    		addMap(node.zeroChild, bitString + "0");
    		addMap(node.oneChild, bitString + "1");		 
    	}
    }
    
    private byte[] changeBytes (String word) {
    	
    	// new ourtput for byte arrays
    	ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
    	
    	// Loop while length is multiple of 8 and not equal to 0
    	while (word.length()%8 != 0) {
    		
    		// If binary string is not 8, add 0
    		word += "0";
    	}
    	for(int index = 0; index < word.length(); index += 8) {
    		
    		// convert 8bit chunks to byte
    		String chunks = word.substring(index, index + 8); 
    		
    		// New int for parsing
    		int parsingInt = Integer.parseInt(chunks, 2); 
    		
    		// write the parse int into byte array output var
    		byteArrayOutput.write((byte) parsingInt);
    	}
    	
    	// return byte array output
    	return byteArrayOutput.toByteArray();
    }
    
    private static PriorityQueue<HuffNode> addQueue (Map<Character, Integer> map) {
    	
    	// create priority queue
    	PriorityQueue<HuffNode> newQueue = new PriorityQueue<HuffNode>();
    	for (Map.Entry<Character, Integer> entry : map.entrySet()) {
    		
    		// Create new huffnode object for each character
    		HuffNode node = new HuffNode(entry.getKey(), entry.getValue());
    		
    		// add node to priority queue
    		newQueue.add(node);
    	}
    	
    	// return priority queue
    	return newQueue; 
    }

}

// ===================================================
// >>> [TN] Summary
// Excellent submission that has a ton to like and was
// obviously well-tested. Generally clean style (apart
// from a few quibbles noted above), and shows
// strong command of programming foundations alongside
// data structure and algorithmic concepts. Keep up
// the great work!
// ---------------------------------------------------
// >>> [TN] Style Checklist
// [X] = Good, [~] = Mixed bag, [ ] = Needs improvement
//
// [X] Variables and helper methods named and used well
// [X] Proper and consistent indentation and spacing
// [X] Proper JavaDocs provided for ALL methods
// [X] Logic is adequately simplified
// [X] Code repetition is kept to a minimum
// ---------------------------------------------------
// Correctness:          98.5 / 100 (-1.5 / missed test)
// Style Penalty:         -1.5
// Total:                97 / 100
// ===================================================
