/*
 * Copyright (C) 2016 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package okhttp3;

import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLPeerUnverifiedException;
import okhttp3.internal.HeldCertificate;
import okhttp3.internal.tls.CertificateChainCleaner;
import okhttp3.internal.tls.RealTrustRootIndex;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public final class CertificateChainCleanerTest {
  @Test public void normalizeSingleSelfSignedCertificate() throws Exception {
    HeldCertificate root = new HeldCertificate.Builder()
        .serialNumber("1")
        .build();
    CertificateChainCleaner council = new CertificateChainCleaner(
        new RealTrustRootIndex(root.certificate));
    assertEquals(list(root), council.clean(list(root)));
  }

  @Test public void normalizeUnknownSelfSignedCertificate() throws Exception {
    HeldCertificate root = new HeldCertificate.Builder()
        .serialNumber("1")
        .build();
    CertificateChainCleaner council = new CertificateChainCleaner(new RealTrustRootIndex());

    try {
      council.clean(list(root));
      fail();
    } catch (SSLPeerUnverifiedException expected) {
    }
  }

  @Test public void orderedChainOfCertificatesWithRoot() throws Exception {
    HeldCertificate root = new HeldCertificate.Builder()
        .serialNumber("1")
        .build();
    HeldCertificate certA = new HeldCertificate.Builder()
        .serialNumber("2")
        .issuedBy(root)
        .build();
    HeldCertificate certB = new HeldCertificate.Builder()
        .serialNumber("3")
        .issuedBy(certA)
        .build();

    CertificateChainCleaner council = new CertificateChainCleaner(
        new RealTrustRootIndex(root.certificate));
    assertEquals(list(certB, certA, root), council.clean(list(certB, certA, root)));
  }

  @Test public void orderedChainOfCertificatesWithoutRoot() throws Exception {
    HeldCertificate root = new HeldCertificate.Builder()
        .serialNumber("1")
        .build();
    HeldCertificate certA = new HeldCertificate.Builder()
        .serialNumber("2")
        .issuedBy(root)
        .build();
    HeldCertificate certB = new HeldCertificate.Builder()
        .serialNumber("3")
        .issuedBy(certA)
        .build();

    CertificateChainCleaner council = new CertificateChainCleaner(
        new RealTrustRootIndex(root.certificate));
    assertEquals(list(certB, certA, root), council.clean(list(certB, certA))); // Root is added!
  }

  @Test public void unorderedChainOfCertificatesWithRoot() throws Exception {
    HeldCertificate root = new HeldCertificate.Builder()
        .serialNumber("1")
        .build();
    HeldCertificate certA = new HeldCertificate.Builder()
        .serialNumber("2")
        .issuedBy(root)
        .build();
    HeldCertificate certB = new HeldCertificate.Builder()
        .serialNumber("3")
        .issuedBy(certA)
        .build();
    HeldCertificate certC = new HeldCertificate.Builder()
        .serialNumber("4")
        .issuedBy(certB)
        .build();

    CertificateChainCleaner council = new CertificateChainCleaner(
        new RealTrustRootIndex(root.certificate));
    assertEquals(list(certC, certB, certA, root), council.clean(list(certC, certA, root, certB)));
  }

  @Test public void unorderedChainOfCertificatesWithoutRoot() throws Exception {
    HeldCertificate root = new HeldCertificate.Builder()
        .serialNumber("1")
        .build();
    HeldCertificate certA = new HeldCertificate.Builder()
        .serialNumber("2")
        .issuedBy(root)
        .build();
    HeldCertificate certB = new HeldCertificate.Builder()
        .serialNumber("3")
        .issuedBy(certA)
        .build();
    HeldCertificate certC = new HeldCertificate.Builder()
        .serialNumber("4")
        .issuedBy(certB)
        .build();

    CertificateChainCleaner council = new CertificateChainCleaner(
        new RealTrustRootIndex(root.certificate));
    assertEquals(list(certC, certB, certA, root), council.clean(list(certC, certA, certB)));
  }

  @Test public void unrelatedCertificatesAreOmitted() throws Exception {
    HeldCertificate root = new HeldCertificate.Builder()
        .serialNumber("1")
        .build();
    HeldCertificate certA = new HeldCertificate.Builder()
        .serialNumber("2")
        .issuedBy(root)
        .build();
    HeldCertificate certB = new HeldCertificate.Builder()
        .serialNumber("3")
        .issuedBy(certA)
        .build();
    HeldCertificate certUnnecessary = new HeldCertificate.Builder()
        .serialNumber("4")
        .build();

    CertificateChainCleaner council = new CertificateChainCleaner(
        new RealTrustRootIndex(root.certificate));
    assertEquals(list(certB, certA, root),
        council.clean(list(certB, certUnnecessary, certA, root)));
  }

  @Test public void chainGoesAllTheWayToSelfSignedRoot() throws Exception {
    HeldCertificate selfSigned = new HeldCertificate.Builder()
        .serialNumber("1")
        .build();
    HeldCertificate trusted = new HeldCertificate.Builder()
        .serialNumber("2")
        .issuedBy(selfSigned)
        .build();
    HeldCertificate certA = new HeldCertificate.Builder()
        .serialNumber("3")
        .issuedBy(trusted)
        .build();
    HeldCertificate certB = new HeldCertificate.Builder()
        .serialNumber("4")
        .issuedBy(certA)
        .build();

    CertificateChainCleaner council = new CertificateChainCleaner(
        new RealTrustRootIndex(selfSigned.certificate, trusted.certificate));
    assertEquals(list(certB, certA, trusted, selfSigned),
        council.clean(list(certB, certA)));
    assertEquals(list(certB, certA, trusted, selfSigned),
        council.clean(list(certB, certA, trusted)));
    assertEquals(list(certB, certA, trusted, selfSigned),
        council.clean(list(certB, certA, trusted, selfSigned)));
  }

  private List<Certificate> list(HeldCertificate... heldCertificates) {
    List<Certificate> result = new ArrayList<>();
    for (HeldCertificate heldCertificate : heldCertificates) {
      result.add(heldCertificate.certificate);
    }
    return result;
  }
}
