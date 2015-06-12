# Usage #

## Add the `@Entity` Annotation to your classes ##

> For `guice-ify` to work, you need to mark your entity classes with `@Entity` annotations.  Both `Objectify` and `javax.persistence` varieties will work.  (`Objectify` itself doesn't impose this requirement, but we need it here because we're using an annotation processor).
```
package guicify.example;

import com.googlecode.objectify.annotation.Entity;
import javax.persistence.Id;

@Entity
public class MyEntity {
    @Id Long id;
    String key;
    String name;
    
    // Getters and setters

}
```

> Will produce a utility class for registering your entities:
```
package guicify.example;

public class ExampleObjectifyRegistry {

  public static void registerToFactory(com.googlecode.objectify.ObjectifyFactory fact){
    fact.register(MyEntity.class);
  }

  public static void register(){
    registerToFactory(com.googlecode.objectify.ObjectifyService.factory());
  }

}
```


> And will also produce a Guice Module that binds the Queries using TypeLiterals so that the correct queries can be specified for injection using generics.
```
package guicify.example;

import com.google.inject.TypeLiteral;
import com.googlecode.objectify.Query;

public class ExampleQueryModule extends com.googlecode.objectify.guice.AbstractQueryModule{

  @Override
  protected void config() {
    bindQuery( new TypeLiteral<Query<MyEntity>>() {}, MyEntity.class );
  }

}
```

> The processor will create one Module and one Registration Utility Class per package.  It will contain a binding for each entity within that package. The name of the generated classes depends on the package name.

## Inject Queries ##

> In one of your modules, you'll need to bind a provider to the `Objectify` class.
```
public void MyModule extends AbstractModule{
    @Override
    protected void configure() {
         bind(Objectify.class).toProvider(new ObjectifyProvider());
    }
}
```

> Finally! Now you can use Objectify Queries as injectable dependencies in your worker objects.
```

public class FindMyEntityQueryWorker {
    //For single-use workers we can inject Query objects directly, otherwise use providers.
    @Inject Query<MyEntity> query;

    public void doStuff {
        query.get().... // doStuff here.
    }
}
```