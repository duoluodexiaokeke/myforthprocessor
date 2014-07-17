/*
 * @(#)Node.java	1.3 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.plugin.dom.core;

import org.w3c.dom.*;
import sun.plugin.dom.*;
import sun.plugin.dom.exception.*;

/**
 *  An abstract class that implements org.w3c.dom.Node.
 */
public abstract class Node implements org.w3c.dom.Node 
{
    // DOM element type
    protected int type;

    // Underlying DOM object.
    protected DOMObject obj;

    /**
     * Construct a new Node object.
     */
    protected Node(int type, DOMObject obj)
    {
	this.type = type;
	this.obj = obj;
    }

    /**
     *  The name of this node, depending on its type; see the table above. 
     */
    public abstract String getNodeName();

    /**
     *  The value of this node, depending on its type; see the table above. 
     * When it is defined to be <code>null</code> , setting it has no effect.
     * @exception DOMException
     *    NO_MODIFICATION_ALLOWED_ERR: Raised when the node is readonly.
     * @exception DOMException
     *    DOMSTRING_SIZE_ERR: Raised when it would return more characters 
     *   than fit in a <code>DOMString</code> variable on the implementation 
     *   platform.
     */
    public abstract String getNodeValue()
                                  throws DOMException;

    public abstract void setNodeValue(String nodeValue)
                                  throws DOMException;

    /**
     *  A code representing the type of the underlying object, as defined 
     * above.
     */
    public abstract short getNodeType();

    /**
     *  The parent of this node. All nodes, except <code>Attr</code> , 
     * <code>Document</code> , <code>DocumentFragment</code> , 
     * <code>Entity</code> , and <code>Notation</code> may have a parent. 
     * However, if a	node has just been created and not yet added to the 
     * tree, or if it has been removed from the tree, this is 
     * <code>null</code> .
     */
    public org.w3c.dom.Node getParentNode()
    {
	return null;
    }

    /**
     *  A <code>NodeList</code> that contains all children of this node. If 
     * there are no children, this is a <code>NodeList</code> containing no 
     * nodes.
     */
    public org.w3c.dom.NodeList getChildNodes()
    {
	return null;
    }

    /**
     *  The first child of this node. If there is no such node, this returns 
     * <code>null</code> .
     */
    public org.w3c.dom.Node getFirstChild()
    {
	return null;
    }

    /**
     *  The last child of this node. If there is no such node, this returns 
     * <code>null</code> .
     */
    public org.w3c.dom.Node getLastChild()
    {
	return null;
    }

    /**
     *  The node immediately preceding this node. If there is no such node, 
     * this returns <code>null</code> .
     */
    public org.w3c.dom.Node getPreviousSibling()
    {
	return null;
    }

    /**
     *  The node immediately following this node. If there is no such node, 
     * this returns <code>null</code> .
     */
    public org.w3c.dom.Node getNextSibling()
    {
	return null;
    }


    /**
     *  A <code>NamedNodeMap</code> containing the attributes of this node (if 
     * it is an <code>Element</code> ) or <code>null</code> otherwise. 
     */
    public org.w3c.dom.NamedNodeMap getAttributes()
    {
	return null;
    }

    /**
     *  The <code>Document</code> object associated with this node. This is 
     * also the <code>Document</code> object used to create new nodes. When 
     * this node is a <code>Document</code> or a <code>DocumentType</code> 
     * which is not used with any <code>Document</code> yet, this is 
     * <code>null</code> .
     * @version DOM Level 2
     */
    public abstract org.w3c.dom.Document getOwnerDocument();

    /**
     *  Inserts the node <code>newChild</code> before the existing child node 
     * <code>refChild</code> . If <code>refChild</code> is <code>null</code> 
     * , insert <code>newChild</code> at the end of the list of children.
     * <br> If <code>newChild</code> is a <code>DocumentFragment</code> 
     * object, all of its children are inserted, in the same order, before 
     * <code>refChild</code> . If the <code>newChild</code> is already in the 
     * tree, it is first removed.
     * @param newChild  The node to insert.
     * @param refChild  The reference node, i.e., the node before which the 
     *   new node must be inserted.
     * @return  The node being inserted.
     * @exception DOMException
     *    HIERARCHY_REQUEST_ERR: Raised if this node is of a type that does 
     *   not allow children of the type of the <code>newChild</code> node, or 
     *   if the node to insert is one of this node's ancestors.
     *   <br> WRONG_DOCUMENT_ERR: Raised if <code>newChild</code> was created 
     *   from a different document than the one that created this node.
     *   <br> NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly or 
     *   if the parent of the node being inserted is readonly.
     *   <br> NOT_FOUND_ERR: Raised if <code>refChild</code> is not a child 
     *   of this node.
     */
    public org.w3c.dom.Node insertBefore(org.w3c.dom.Node newChild, 
					 org.w3c.dom.Node refChild)
					    throws DOMException
    {
	throw new HierarchyRequestException("Node.insertBefore() is not supported");
    }

    /**
     *  Replaces the child node <code>oldChild</code> with 
     * <code>newChild</code> in the list of children, and returns the 
     * <code>oldChild</code> node.
     * <br> If <code>newChild</code> is a <code>DocumentFragment</code> object,
     *  <code>oldChild</code> is replaced by all of the 
     * <code>DocumentFragment</code> children, which are inserted in the same 
     * order. If the <code>newChild</code> is already in the tree, it is 
     * first removed.
     * @param newChild  The new node to put in the child list.
     * @param oldChild  The node being replaced in the list.
     * @return  The node replaced.
     * @exception DOMException
     *    HIERARCHY_REQUEST_ERR: Raised if this node is of a type that does 
     *   not allow children of the type of the <code>newChild</code> node, or 
     *   if the node to put in is one of this node's ancestors.
     *   <br> WRONG_DOCUMENT_ERR: Raised if <code>newChild</code> was created 
     *   from a different document than the one that created this node.
     *   <br> NO_MODIFICATION_ALLOWED_ERR: Raised if this node or the parent 
     *   of the new node is readonly.
     *   <br> NOT_FOUND_ERR: Raised if <code>oldChild</code> is not a child 
     *   of this node.
     */
    public org.w3c.dom.Node replaceChild(org.w3c.dom.Node newChild, 
					 org.w3c.dom.Node oldChild)
					 throws DOMException
    {
       	throw new HierarchyRequestException("Node.replaceChild() is not supported");
    }

    /**
     *  Removes the child node indicated by <code>oldChild</code> from the 
     * list of children, and returns it.
     * @param oldChild  The node being removed.
     * @return  The node removed.
     * @exception DOMException
     *    NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     *   <br> NOT_FOUND_ERR: Raised if <code>oldChild</code> is not a child 
     *   of this node.
     */
    public org.w3c.dom.Node removeChild(org.w3c.dom.Node oldChild)
                            throws DOMException
    {
	throw new NoModificationAllowedException("Node.removeChild() is not supported");
    }

    /**
     *  Adds the node <code>newChild</code> to the end of the list of children 
     * of this node. If the <code>newChild</code> is already in the tree, it 
     * is first removed.
     * @param newChild  The node to add. If it is a  
     *   <code>DocumentFragment</code> object, the entire contents of the 
     *   document fragment are moved into the child list of this node
     * @return  The node added.
     * @exception DOMException
     *    HIERARCHY_REQUEST_ERR: Raised if this node is of a type that does 
     *   not allow children of the type of the <code>newChild</code> node, or 
     *   if the node to append is one of this node's ancestors.
     *   <br> WRONG_DOCUMENT_ERR: Raised if <code>newChild</code> was created 
     *   from a different document than the one that created this node.
     *   <br> NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     */
    public org.w3c.dom.Node appendChild(org.w3c.dom.Node newChild)
                            throws DOMException
    {
	throw new NoModificationAllowedException("Node.appendChild() is not supported");
    }

    /**
     *  This is a convenience method to allow easy determination of whether a 
     * node has any children.
     * @return  <code>true</code> if the node has any children, 
     *   <code>false</code> if the node has no children.
     */
    public boolean hasChildNodes()
    {
	return false;
    }

    /**
     *  Returns a duplicate of this node, i.e., serves as a generic copy 
     * constructor for nodes. The duplicate node has no parent; (
     * <code>parentNode</code> is <code>null</code> .).
     * <br> Cloning an <code>Element</code> copies all attributes and their 
     * values, including those generated by the XML processor to represent 
     * defaulted attributes, but this method does not copy any text it 
     * contains unless it is a deep clone, since the text is contained in a 
     * child <code>Text</code> node. Cloning an <code>Attribute</code> 
     * directly, as opposed to be cloned as part of an <code>Element</code> 
     * cloning operation, returns a specified attribute (
     * <code>specified</code> is <code>true</code> ). Cloning any other type 
     * of node simply returns a copy of this node.
     * <br> Note that cloning an immutable subtree results in a mutable copy, 
     * but the children of an <code>EntityReference</code> clone are   
     * readonly . In addition, clones of unspecified <code>Attr</code> nodes 
     * are specified. And, cloning <code>Document</code> , 
     * <code>DocumentType</code> , <code>Entity</code> , and 
     * <code>Notation</code> nodes is implementation dependent.
     * @param deep  If <code>true</code> , recursively clone the subtree under 
     *   the specified node; if <code>false</code> , clone only the node 
     *   itself (and its attributes, if it is an <code>Element</code> ).  
     * @return  The duplicate node.
     */
    public org.w3c.dom.Node cloneNode(boolean deep)
    {
	return null;
    }

    /**
     *  Puts all <code>Text</code> nodes in the full depth of the sub-tree 
     * underneath this <code>Node</code> , including attribute nodes, into a 
     * "normal" form where only structure (e.g., elements, comments, 
     * processing instructions, CDATA sections, and entity references) 
     * separates <code>Text</code> nodes, i.e., there are neither adjacent 
     * <code>Text</code> nodes nor empty <code>Text</code> nodes. This can be 
     * used to ensure that the DOM view  of a document is the same as if it 
     * were saved and re-loaded, and is useful when operations (such as 
     * XPointer lookups) that depend on a particular document tree structure 
     * are to be used. In cases where the document contains 
     * <code>CDATASections</code> , the normalize operation alone may not be 
     * sufficient, since XPointers do not differentiate between 
     * <code>Text</code> nodes and <code>CDATASection</code> nodes.
     * @since DOM Level 2
     */
    public void normalize()
    {
    }

    /**
     *  Tests whether the DOM implementation implements a specific feature and 
     * that feature is supported by this node.
     * @param feature  The name of the feature to test. This is the same name 
     *   which can be passed to the method <code>hasFeature</code> on 
     *   <code>DOMImplementation</code> .
     * @param version  This is the version number of the feature to test. In 
     *   Level 2, version 1, this is the string "2.0". If the version is not 
     *   specified, supporting any version of the feature will cause the 
     *   method to return <code>true</code> .
     * @return  Returns <code>true</code> if the specified feature is supported
     *    on this node, <code>false</code> otherwise.
     * @since DOM Level 2
     */
    /**
     * Tests whether the DOM implementation implements a specific feature and 
     * that feature is supported by this node.
     * @param featureThe name of the feature to test. This is the same name 
     *   which can be passed to the method <code>hasFeature</code> on 
     *   <code>DOMImplementation</code>.
     * @param versionThis is the version number of the feature to test. In 
     *   Level 2, version 1, this is the string "2.0". If the version is not 
     *   specified, supporting any version of the feature will cause the 
     *   method to return <code>true</code>.
     * @return Returns <code>true</code> if the specified feature is 
     *   supported on this node, <code>false</code> otherwise.
     * @since DOM Level 2
     */
    public boolean isSupported(String feature, 
                               String version)
    {
	// Delegate it to DOMImplementation
	//
	org.w3c.dom.DOMImplementation impl = new sun.plugin.dom.DOMImplementation();

	return impl.hasFeature(feature, version);
    }

    /**
     *  The  namespace URI of this node, or <code>null</code> if it is 
     * unspecified.
     * <br> This is not a computed value that is the result of a namespace 
     * lookup based on an examination of the namespace declarations in scope. 
     * It is merely the namespace URI given at creation time.
     * <br> For nodes of any type other than <code>ELEMENT_NODE</code> and 
     * <code>ATTRIBUTE_NODE</code> and nodes created with a DOM Level 1 
     * method, such as <code>createElement</code> from the 
     * <code>Document</code> interface, this is always <code>null</code> . 
     * Per the  Namespaces in XML Specification  an attribute does not 
     * inherit its namespace from the element it is attached to. If an 
     * attribute is not explicitly given a namespace, it simply has no 
     * namespace.
     * @since DOM Level 2
     */
    public String getNamespaceURI()
    {	
	// Namespace is not supported yet.
	return null;
    }

    /**
     *  The  namespace prefix of this node, or <code>null</code> if it is 
     * unspecified.
     * <br> Note that setting this attribute, when permitted, changes the 
     * <code>nodeName</code> attribute, which holds the  qualified name , as 
     * well as the <code>tagName</code> and <code>name</code> attributes of 
     * the <code>Element</code> and <code>Attr</code> interfaces, when 
     * applicable.
     * <br> Note also that changing the prefix of an attribute that is known to
     *  have a default value, does not make a new attribute with the default 
     * value and the original prefix appear, since the 
     * <code>namespaceURI</code> and <code>localName</code> do not change.
     * <br> For nodes of any type other than <code>ELEMENT_NODE</code> and 
     * <code>ATTRIBUTE_NODE</code> and nodes created with a DOM Level 1 
     * method, such as <code>createElement</code> from the 
     * <code>Document</code> interface, this is always <code>null</code> .
     * @exception DOMException
     *    INVALID_CHARACTER_ERR: Raised if the specified prefix contains an 
     *   illegal character.
     *   <br> NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     *   <br> NAMESPACE_ERR: Raised if the specified <code>prefix</code> is 
     *   malformed, if the <code>namespaceURI</code> of this node is 
     *   <code>null</code> , if the specified prefix is "xml" and the 
     *   <code>namespaceURI</code> of this node is different from " 
     *   http://www.w3.org/XML/1998/namespace ", if this node is an attribute 
     *   and the specified prefix is "xmlns" and the <code>namespaceURI</code>
     *    of this node is different from " http://www.w3.org/2000/xmlns/ ", 
     *   or if this node is an attribute and the <code>qualifiedName</code> 
     *   of this node is "xmlns"  .
     * @since DOM Level 2
     */
    public String getPrefix()
    {
	// Namespace is not supported yet.
	return null;   
    }

    public void setPrefix(String prefix)
                            throws DOMException
    {
	throw new NoModificationAllowedException("Node.setPrefix() is not supported");
    }

    /**
     *  Returns the local part of the  qualified name of this node.
     * <br> For nodes of any type other than <code>ELEMENT_NODE</code> and 
     * <code>ATTRIBUTE_NODE</code> and nodes created with a DOM Level 1 
     * method, such as <code>createElement</code> from the 
     * <code>Document</code> interface, this is always <code>null</code> .
     * @since DOM Level 2
     */
    public String getLocalName()
    {
	return null;
    }

    /**
     * Returns whether this node (if it is an element) has any attributes.
     * @return <code>true</code> if this node has any attributes, 
     *   <code>false</code> otherwise.
     * @since DOM Level 2
     */
    public boolean hasAttributes()
    {
	return true;
    }
}
