<#--
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
-->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#educationalTraining. 
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->

<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>

<#-- Coming from propDelete, individual is not defined, but we are editing. -->
<@showEducationalTraining statement=statement editable=(!individual?? || individual.editable) />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showEducationalTraining statement editable>

    <#local degree>
        <#if statement.degreeName??>
            <#-- RY Giving up on join here. Freemarker insists on removing the space before "in"
                 and leaving no space between the degree and major field, even though compress
                 should only delete consecutive spaces. Even &nbsp; doesn't help.
            <@s.join [ statement.degreeAbbr!statement.degreeName, statement.majorField! ], " in " /> -->
            ${statement.degreeAbbr!statement.degreeName} 
            <#if statement.majorField??> in ${statement.majorField}</#if>
        <#elseif statement.typeName??>
            ${statement.typeName!}
        </#if>
    </#local>
    
    <#local linkedIndividual>
        <#if statement.org??>
            <a href="${profileUrl(statement.uri("org"))}" title="organization name">${statement.orgName}</a>
        <#elseif editable>
            <#-- Show the link to the context node only if the user is editing the page. -->
            <a href="${profileUrl(statement.uri("edTraining"))}" title="missing organization">missing organization</a>
        </#if>
    </#local>

    <@s.join [ degree, linkedIndividual!, statement.deptOrSchool!, statement.info! ] /> <@dt.yearIntervalSpan "${statement.dateTimeStart!}" "${statement.dateTimeEnd!}" false/>

</#macro>