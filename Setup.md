# Setup #

## Add The Repository ##

Add the following repository to your pom.  It contains files for both this project and the custom `objectify-query` build (explained more below).

```
    <repository>
        <id>objectify-guice repo</id>
        <url>http://guice-ify.googlecode.com/hg/repo/</url>
        <layout>default</layout>
    </repository>
```

## Add The Dependencies ##

Add the following dependency to your pom (along with the appropriate repository):

```
    <dependency>
        <groupId>com.googlecode.objectify-guice</groupId>
        <artifactId>objectify-guice</artifactId>
        <version>0.1-SNAPSHOT</version>
    </dependency>

    <dependency>
        <groupId>com.googlecode.objectify-query</groupId>
        <artifactId>objectify-query</artifactId>
        <version>0.2.jrt-SNAPSHOT</version>
    </dependency>
```

**NOTE:** The _objectify-query_ dependency is another googlecode project that builds typesafe Objectify query objects.  The `0.2-jrt-SNAPSHOT` version is a customized build that inserts the @Inject annotations so the generated classes can also be injected using Guice.  My changes are made available in the `objectify_query.patch` file found in version control.  It's not required, but it is handy.  See the [objectify-query](http://code.google.com/p/objectify-query/) project for more information.

## Setup the Annotation Processors ##


You'll then need to setup the annotation processors.  There are a number of maven plugins that can run annotation processors, but if you're using the Java 6 compiler (or higher), you can use the compiler plugin and process the annotations in parallel with compilation (_it's buggy_).  I recommend the plugin below.

You wouldn't usually use all of the processors.  See the table of contents to explore what the various processors do.


```
<plugin>
    <groupId>org.bsc.maven</groupId>
    <artifactId>maven-processor-plugin</artifactId>
    <version>2.0.4</version>
    <executions>
        <execution>
            <id>process</id>
            <goals>
                <goal>process</goal>
            </goals>
            <phase>process-sources</phase>
        </execution>
        <execution>
            <id>process-test</id>
            <goals>
                <goal>process-test</goal>
            </goals>
            <phase>process-test-sources</phase>
        </execution>
    </executions>
    <configuration>
        <processors>
            <processor>com.googlecode.objectify.query.processor.EntityProcessor</processor>
            <processor>com.googlecode.objectify.guice.processor.ObjectifyRegistryProcessor</processor>
            <processor>com.googlecode.objectify.guice.processor.GuiceModuleBuilder</processor>
        </processors>
    </configuration>
</plugin>
```

**Note:** Once again, the `EntityProcessor` is from the optional custom build of _objectify-query_.