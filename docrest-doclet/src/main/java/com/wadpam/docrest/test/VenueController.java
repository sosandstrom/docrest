/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.docrest.test;

import com.wadpam.docrest.domain.RestCode;
import com.wadpam.docrest.domain.RestReturn;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author os
 */
@RestReturn(value=Venue.class)
@Controller
@RequestMapping(value={"{domain}/venue","{domain}/v10/altvenue"})
public class VenueController extends AbstractController<Venue> {
    
    @RequestMapping(value={"{name}","findByName/{name}"}, method= RequestMethod.GET)
    public ResponseEntity<Venue> findByName(@PathVariable String name) {
        
        return new ResponseEntity<Venue>(HttpStatus.NOT_FOUND);
    }
	
    /**
     * Updates an existing Venue, passed in the POST contents.
     * @param request not used
     * @param id The Venue id
     * @return 200 OK
     */
    @RequestMapping(value="{id}", method= RequestMethod.POST)
    public ResponseEntity<Venue> update(HttpServletRequest request, @PathVariable String id) {
        
        return new ResponseEntity<Venue>(HttpStatus.OK);
    }

    @RestReturn(value=Venue.class, code={
        @RestCode(code=200, description="If found", message="OK"),
        @RestCode(code=404, description="Not Found", message="Not Found")})
    @RequestMapping(value="{id}", method= RequestMethod.DELETE)
    public ResponseEntity<Venue> delete(@PathVariable String id) {
        
        return new ResponseEntity<Venue>(HttpStatus.OK);
    }
    
    @RequestMapping(method= RequestMethod.GET)
    public ResponseEntity<Venue> last() {
        return new ResponseEntity<Venue>(HttpStatus.NOT_FOUND);
    }
}
