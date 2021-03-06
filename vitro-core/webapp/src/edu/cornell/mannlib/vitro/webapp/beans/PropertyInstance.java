/*
Copyright (c) 2012, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package edu.cornell.mannlib.vitro.webapp.beans;

import java.util.Date;

/**
   Represents a Vitro object property instance.  It includes values
   from the entities, object property statements, properties, and ent2relationships tables
   bundled up in a usable object.
*/
public class PropertyInstance implements PropertyInstanceIface, Comparable<PropertyInstance> {

    private String propertyURI = null;
    private String objectEntURI = null;
    private String subjectEntURI = null;

    private String subjectName = null;
    private String objectName = null;

    private String propertyName = null;
    private String domainPublic = null;
    private String rangePublic = null;

    private String domainClassName = null;
    private String rangeClassName = null;
    private String domainQuickEditJsp = null;
    private String rangeQuickEditJsp = null;

    private String domainClassURI = null;
    private String rangeClassURI = null;

    private boolean subjectSide = true;

    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#getPropertyId()
     */
    public String getPropertyURI(){return propertyURI;}
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#getDomainName()
     */
    public String getSubjectName(){return subjectName;}
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#getRangeName()
     */
    public String getObjectName(){return objectName;}
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#getRangeClass()
     */
    public String getRangeClassURI(){return rangeClassURI;}
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#getDomainClass()
     */
    public String getDomainClassURI(){return domainClassURI;}
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#getDomainClassName()
     */
    public String getDomainClassName(){return domainClassName;}
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#getRangeClassName()
     */
    public String getRangeClassName(){return rangeClassName;}
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#getRangeEntId()
     */
    public String getObjectEntURI(){return objectEntURI;}
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#getDomainEntId()
     */
    public String getSubjectEntURI(){return subjectEntURI;}
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#getPropertyName()
     */
    public String getPropertyName(){return propertyName;}
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#getDomainPublic()
     */
    public String getDomainPublic(){return domainPublic;}
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#getRangePublic()
     */
    public String getRangePublic(){return rangePublic;}
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#getQualifier()
     */
    public boolean getSubjectSide(){return subjectSide;}
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#getDomainQuickEditJsp()
     */
    public String getDomainQuickEditJsp(){return domainQuickEditJsp; }
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#getRangeQuickEditJsp()
     */
    public String getRangeQuickEditJsp(){return rangeQuickEditJsp; }

    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#setPropertyId(int)
     */
    public void setPropertyURI(String in){propertyURI = in;}
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#setDomainName(java.lang.String)
     */
    public void setSubjectName(String in){subjectName=in; }
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#setRangeName(java.lang.String)
     */
    public void setObjectName(String in){objectName=in;}
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#setRangeClass(int)
     */
    public void setRangeClassURI(String in){rangeClassURI=in;}
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#setDomainClass(int)
     */
    public void setDomainClassURI(String in){domainClassURI=in;}
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#setDomainClassName(java.lang.String)
     */
    public void setDomainClassName(String in){domainClassName=in; }
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#setRangeClassName(java.lang.String)
     */
    public void setRangeClassName(String in){rangeClassName=in;}
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#setRangeEntId(int)
     */
    public void setObjectEntURI(String in){objectEntURI=in;}
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#setDomainEntId(int)
     */
    public void setSubjectEntURI(String in){subjectEntURI=in;}
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#setPropertyName(java.lang.String)
     */
    public void setPropertyName(String in){propertyName=in;}
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#setDomainPublic(java.lang.String)
     */
    public void setDomainPublic(String in){domainPublic=in;}
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#setRangePublic(java.lang.String)
     */
    public void setRangePublic(String in){rangePublic=in;}
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#setDomainSide(boolean)
     */
    public void setSubjectSide(boolean in){subjectSide=in;}
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#setDomainQuickEditJsp(java.lang.String)
     */
    public void setDomainQuickEditJsp(String in){ domainQuickEditJsp = in; }
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.beans.PropertyInstanceIface#setRangeQuickEditJsp(java.lang.String)
     */
    public void setRangeQuickEditJsp(String in){ rangeQuickEditJsp = in; }

    public static final int DOMAINSIDE = 0; //property has some entity of interest as domainId
    public static final int RANGESIDE = 1; //property has some entity of interest as rangeId

    @Override
    public String toString(){
        //this takes getDomainSide into account and hides much of the datastruct (and bugs)
        String out = "PropInst(" + getPropertyURI() +")";
        if( getSubjectSide() ){
            out = getDomainClassName() + "("+ getDomainClassURI() + ") ";
            out += getDomainPublic() + " ";
            out += getRangeClassName() + "(" + getRangeClassURI() + ")";
        }else{
            out += getRangeClassName() + "(" + getRangeClassURI() + ") ";
            out += getRangePublic() + " ";
            out = getDomainClassName() + "("+ getDomainClassURI() + ")";
        }
        return out;
    }
    
    public int compareTo(PropertyInstance pi) {

        try {
            if (this.getDomainPublic().equals(pi.getDomainPublic())) {
                return this.getRangeClassName().compareTo(pi.getRangeClassName());
            } else {
                return (this.getDomainPublic().compareTo(pi.getDomainPublic()));
            }
        } catch (NullPointerException npe) {
            return -1;
        }

    }
}
