package name.psarek.gddttask.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {

    private final String entityType;
    private final Object entityKey;

    public EntityNotFoundException(String entityType, Object entityKey) {
        this.entityType = entityType;
        this.entityKey = entityKey;
    }

    @Override
    public String getMessage() {
        return "Entity of type " + entityType + " identified by key '" + entityKey + "' was not found";
    }

}
