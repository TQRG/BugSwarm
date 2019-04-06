package foo;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@ComponentScan({"src.test.files.checks.spring.A", "src.test.files.checks.spring.B"})
class Foo1 {

}

@Configuration
@ComponentScan("src.test.files.checks.spring.X")
class Foo2 {

}

@Configuration
@ComponentScan(basePackageClasses = Bar4.class, basePackages = {"src.test.files.checks.spring.Y1"})
class Foo3 {

}

@Configuration
@ComponentScan(basePackages = "src.Y2")
class Foo4 {

}

@Configuration
@ComponentScan(value = "src.test.files.checks.spring.Z1")
class Foo5 {

}

@Configuration
@ComponentScan(value = {"src.test.files.checks.spring.Z2", "src.test.files.checks.spring.Z3"})
class Foo6 {

}

@Component
class Bar1 { } // Noncompliant

@Service
class Bar2 { } // Noncompliant

@Controller
class Bar3 { } // Noncompliant

@RestController
class Bar4 { } // Noncompliant, we ignore basePackageClasses
