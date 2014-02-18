/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.docrest.test;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.wadpam.docrest.domain.RestCode;
import com.wadpam.docrest.domain.RestReturn;

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
    public ResponseEntity<List<T>> findAll(@RequestParam(defaultValue="0", required=false) int offset,
            @RequestParam(defaultValue="10", required=false) int limit) {
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
    public ResponseEntity<List<T>> findAllPost(@RequestParam(defaultValue="0", required=false) int offset,
            @RequestParam(defaultValue="10", required=false) int limit) {
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
    
    /**
     * Creates an Entity from the JSON body, 
     * and redirects to the created Entity.
     * @param request
     * @param response
     * @param model
     * @param jEntity The JSON body will be bound to this parameter
     * @return
     */
    @RestReturn(value=URI.class, code={
        @RestCode(code=200, description="Entity created", message="OK ")
    })
    @RequestMapping(value="v10", method=RequestMethod.POST)
    public ResponseEntity<T> createFromJson(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            Model model,
            @RequestBody T jEntity) {
        
        return new ResponseEntity<T>(HttpStatus.OK);
    }
}
