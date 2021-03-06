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

package edu.cornell.mannlib.vitro.webapp.rdfservice.impl;

import java.io.InputStream;

import edu.cornell.mannlib.vitro.webapp.rdfservice.ModelChange;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;

public class ModelChangeImpl implements ModelChange {

	private InputStream serializedModel;
	private RDFService.ModelSerializationFormat serializationFormat;
	private Operation operation;
	private String graphURI;

	public ModelChangeImpl() {}
	
	public ModelChangeImpl(InputStream serializedModel,
                           RDFService.ModelSerializationFormat serializationFormat,
                           Operation operation,
                           String graphURI) {
		
		this.serializedModel = serializedModel;
		this.serializationFormat = serializationFormat;
		this.operation = operation;
		this.graphURI = graphURI;
	}

	@Override
	public InputStream getSerializedModel() {
		return serializedModel;
	}
	
	@Override
	public void setSerializedModel(InputStream serializedModel) {
		this.serializedModel = serializedModel;
	}
	
	@Override
	public RDFService.ModelSerializationFormat getSerializationFormat() {
		return serializationFormat;
	}
	
	@Override
	public void setSerializationFormat(RDFService.ModelSerializationFormat serializationFormat) {
		this.serializationFormat = serializationFormat;
	}
	
	@Override
	public Operation getOperation() {
		return operation;
	}
	
	@Override
	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	@Override
	public String getGraphURI() {
		return graphURI;
	}

	@Override
	public void setGraphURI(String graphURI) {
		this.graphURI = graphURI;
	}
}
