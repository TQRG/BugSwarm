//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.11.18 at 01:00:50 AM GMT 
//


package org.ikasan.component.converter.xml.jaxb;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="one" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="two" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "one",
    "two"
})
@XmlRootElement(name = "example")
public class Example {

    public Example()
    {
        super();
    }

    public Example(String one, String two)
    {
        super();
        this.one = one;
        this.two = two;
    }

    @XmlJavaTypeAdapter(DoSomethingXmlAdapter.class)
    @XmlElement(required = true)
    protected String one;
    @XmlJavaTypeAdapter(DoSomethingXmlAdapter.class)
    @XmlElement(required = true)
    protected String two;

    /**
     * Gets the value of the one property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOne() {
        return one;
    }

    /**
     * Sets the value of the one property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOne(String value) {
        this.one = value;
    }

    /**
     * Gets the value of the two property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTwo() {
        return two;
    }

    /**
     * Sets the value of the two property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTwo(String value) {
        this.two = value;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((one == null) ? 0 : one.hashCode());
        result = prime * result + ((two == null) ? 0 : two.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Example other = (Example) obj;
        if (one == null)
        {
            if (other.one != null) return false;
        }
        else if (!one.equals(other.one)) return false;
        if (two == null)
        {
            if (other.two != null) return false;
        }
        else if (!two.equals(other.two)) return false;
        return true;
    }

}
