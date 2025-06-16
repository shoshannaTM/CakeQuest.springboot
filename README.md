# CakeQuest.springboot
CakeQuest is a server side rendered Spring Boot web application that helps bakers organize and track their custom cake projects from start to finish.
With this application a baker is able to:
Create and manage custom cake projects


• Save a database of recipes, either imported via URL or created in app


• Select flavors for the cake, filling, and frosting from recipes they’ve saved


• View auto generated consolidated shopping lists for a cake project that merges quantities across all components


• Shop their pantry to update the auto generated shopping list and avoid duplicate purchases


• See a consolidated "store mode" shopping list that accounts for pantry stock


• Manage and customize cake baking timelines and receive notifications of task due dates


• Mark each component as complete


• Mark the cake as fully assembled and optionally upload a final photo


• Access a history of all cakes they’ve made

# Three Tiered Architecture
```mermaid
flowchart TD
 subgraph PresentationLayer["UI Layer (ThymeLeaf)"]
        Baker["Baker Interface"]
  end
 subgraph BusinessLogicLayer["Business Logic (Springboot)"]
        Server["MVC Server"]
  end
 subgraph DataLayer["Data Layer (Hibernate)"]
        Database["Database"]
  end
    Baker --> Server
    Server --> Database
```
# ERD
```mermaid
erDiagram
    app_user {
        int user_id
        varchar(255) name
        varchar(255) email
        varchar(255) password
    }
    cake_order {
        int cake_id
        int user_id
        text notes
        enum status
        int cake_recipe_id
        int filling_recipe_id
        int frosting_recipe_id
        datetime due_date
    }
    recipe {
        int recipe_id
        varchar(255) recipe_name
        enum recipe_type
        text instructions
    }
    recipe_ingredient {
        int recipe_id
        int ingredient_id
        int quantity
        enum measurement_unit
    }
    ingredient {
        int ingredient_id
        varchar(255) ingredient_name
    }
 

    app_user ||--o{ cake_order : creates
    cake_order }|--o{ recipe : references
    recipe }|--|{ recipe_ingredient : contains
    recipe_ingredient }|--|{ ingredient : is
```
