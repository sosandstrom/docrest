/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.docrest.test;

import com.wadpam.docrest.domain.RestCode;
import com.wadpam.docrest.domain.RestReturn;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author os
 */
public class AbstractController<T> {
    
    /**
     * Retrieves all entities of type T 
     * @param offset skip this number of entities before returning. Used for paging. Default is 0.
     * @param limit return this number of entities. Used for paging. Default is 10.
     * @return 
     */
    @RestReturn(value=List.class)
    @RequestMapping(value="", method= RequestMethod.GET)
    public ResponseEntity<List<T>> findAll(@RequestParam(defaultValue="0") int offset,
            @RequestParam(defaultValue="10") int limit) {
        return new ResponseEntity<List<T>>(HttpStatus.NOT_FOUND);
    }

	 /**
     * Lee's post test, Retrieves all entities of type T 
     * @param offset skip this number of entities before returning. Used for paging. Default is 0.
     * @param limit return this number of entities. Used for paging. Default is 10.
     * @return 
     */
    @RestReturn(value=List.class)
    @RequestMapping(value="", method= RequestMethod.POST)
    public ResponseEntity<List<T>> findAllPost(@RequestParam(defaultValue="0") int offset,
            @RequestParam(defaultValue="10") int limit) {
        return new ResponseEntity<List<T>>(HttpStatus.NOT_FOUND);
    }
    
    /**
     * Retrieves the first entity of type T 
     * @return 
     */
    @RestReturn(value=ResponseEntity.class, code={@RestCode(code=200, description="When a first Entity exists")})
    @RequestMapping(value="1st", method= RequestMethod.GET)
    public ResponseEntity<T> first() {
        return new ResponseEntity<T>(HttpStatus.NOT_FOUND);
    }
    
}
