<!DOCTYPE html>
<html>
    <head/>
    <body>
        <h1>
            ${blogTitle}
        </h1>
        <p>
            ${message}
        </p>
        <h3>
            References
        </h3>
        <p>
            <#list references as reference>
                ${reference_index + 1}. ${reference} <br/>
            </#list>
        </p>
    </body>
</html>