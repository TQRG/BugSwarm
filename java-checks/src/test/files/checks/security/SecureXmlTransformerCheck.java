import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.XMLConstants;

class A {

  TransformerFactory no_call_to_securing_method() {
    TransformerFactory factory = TransformerFactory.newInstance(); // Noncompliant
    return factory;
  }

  TransformerFactory secure_processing_true() {
    TransformerFactory factory = TransformerFactory.newInstance();
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    return factory;
  }

  TransformerFactory secure_processing_false() {
    TransformerFactory factory = TransformerFactory.newInstance(); // Noncompliant
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
    return factory;
  }

  TransformerFactory secure_processing_true_with_literal() {
    TransformerFactory factory = TransformerFactory.newInstance();
    factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
    return factory;
  }

  TransformerFactory secure_processing_true() {
    TransformerFactory factory = TransformerFactory.newInstance(); // Noncompliant
    factory.setFeature(XMLConstants.ACCESS_EXTERNAL_DTD, true);
    return factory;
  }

}
