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
package uk.ac.manchester.cs.owl.owlapi;

import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.util.NNF;
import org.semanticweb.owlapi.util.OWLObjectTypeIndexProvider;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.0.0
 */
public abstract class OWLAxiomImpl extends OWLObjectImpl implements OWLAxiom, CollectionContainer<OWLAnnotation> {

    protected final @Nonnull List<OWLAnnotation> annotations;

    @Override
    protected int index() {
        return OWLObjectTypeIndexProvider.AXIOM_TYPE_INDEX_BASE + getAxiomType().getIndex();
    }

    /**
     * @param annotations
     *        annotations on the axiom
     */
    public OWLAxiomImpl(Collection<OWLAnnotation> annotations) {
        checkNotNull(annotations, "annotations cannot be null");
        this.annotations = asAnnotations(annotations);
    }

    @Override
    public Stream<OWLAnnotation> annotations() {
        return annotations.stream();
    }

    @Override
    public boolean isAnnotated() {
        return !annotations.isEmpty();
    }

    @Override
    public void accept(CollectionContainerVisitor<OWLAnnotation> t) {
        annotations.forEach(a -> t.visitItem(a));
    }

    /**
     * A convenience method for implementation that returns a set containing the
     * annotations on this axiom plus the annotations in the specified set.
     * 
     * @param annos
     *        The annotations to add to the annotations on this axiom
     * @return The annotations
     */
    protected Collection<OWLAnnotation> mergeAnnos(Stream<OWLAnnotation> annos) {
        return asSet(Stream.concat(annos, annotations.stream()));
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || hashCode() != obj.hashCode()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof OWLAxiom)) {
            return false;
        }
        OWLAxiom other = (OWLAxiom) obj;
        // for OWLAxiomImpl comparisons, do not create wrapper objects
        if (other instanceof OWLAxiomImpl) {
            return annotations.equals(((OWLAxiomImpl) other).annotations);
        }
        return equalStreams(annotations(), annotations());
    }

    @Override
    public OWLAxiom getNNF() {
        return accept(new NNF(new OWLDataFactoryImpl()));
    }
}
