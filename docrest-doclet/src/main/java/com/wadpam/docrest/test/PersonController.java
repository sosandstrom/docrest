/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.docrest.test;

import com.wadpam.docrest.domain.RestCode;
import com.wadpam.docrest.domain.RestReturn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This is the PersonController javadoc text, containing a few <b>HTML tags</b>.
 * @param stores optional inner object. boolean
 * @author os
 */
@RestReturn(value=Venue.class)
@Controller
@RequestMapping(value="person")
public class PersonController extends AbstractController {

    /**
     * findByName returns all Venues with the specified name
     * @param name the specified name
     * @return all Venues with the specified name
     * @since 1.23
     */
    @RestReturn(value=Venue.class, 
        code={@RestCode(code=200, description="When found"), 
        @RestCode(code=404, description="When no user exists for specified name", message="NOT FOUND")},
        supportsClassParams = true)
    @RequestMapping(value="{name}", method= {RequestMethod.GET,RequestMethod.POST})
    public ResponseEntity<Venue> findByName(@PathVariable String name,
            @RequestParam(value="p1",defaultValue="staff") String position,
            @RequestParam(value="p2") Boolean test) {
        
        return new ResponseEntity<Venue>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value="{id}", method= RequestMethod.DELETE)
    public ResponseEntity<Venue> delete(@PathVariable String id) {
        
        return new ResponseEntity<Venue>(HttpStatus.OK);
    }
    
    /**
     * Method comment for update with ModelAttributes
     * @param person this is the javadoc comment for person ModelAttribute
     * @param location this is the javadoc for location ModelAttribute
     * @return 
     */
    @RestReturn(value=Venue.class, 
        code={@RestCode(code=200, description="When found"), 
        @RestCode(code=404, description="When no user exists for specified name", message="NOT FOUND")})
    @RequestMapping(value="{name}", method= RequestMethod.POST)
    public ResponseEntity<Venue> update(@ModelAttribute Venue person, 
            @ModelAttribute Location location) {
        return null;
    }
}
