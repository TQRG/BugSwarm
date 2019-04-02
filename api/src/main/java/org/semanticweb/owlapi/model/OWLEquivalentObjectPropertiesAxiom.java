/* This file is part of the OWL API.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright 2014, The University of Manchester
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. */
package org.semanticweb.owlapi.model;

import java.util.Collection;

/**
 * Represents
 * <a href= "http://www.w3.org/TR/owl2-syntax/#Equivalent_Object_Properties" >
 * EquivalentObjectProperties</a> axioms in the OWL 2 specification.
 * 
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.0.0
 */
public interface OWLEquivalentObjectPropertiesAxiom
    extends OWLNaryPropertyAxiom<OWLObjectPropertyExpression>, OWLObjectPropertyAxiom {

    @Override
        OWLEquivalentObjectPropertiesAxiom getAxiomWithoutAnnotations();

    @Override
    default int hashIndex() {
        return 61;
    }

    @Override
        Collection<OWLEquivalentObjectPropertiesAxiom> asPairwiseAxioms();

    @Override
        Collection<OWLEquivalentObjectPropertiesAxiom> splitToAnnotatedPairs();

    /**
     * @return the axioms equivalent to the equivalent object property axiom
     */
    Collection<OWLSubObjectPropertyOfAxiom> asSubObjectPropertyOfAxioms();

    @Override
    default void accept(OWLObjectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    default <O> O accept(OWLObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Override
    default void accept(OWLAxiomVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    default <O> O accept(OWLAxiomVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Override
    default AxiomType<?> getAxiomType() {
        return AxiomType.EQUIVALENT_OBJECT_PROPERTIES;
    }
}
