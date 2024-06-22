package com.emma.Ecommerce.api.controller.user;

import com.emma.Ecommerce.model.Address;
import com.emma.Ecommerce.model.LocalUser;
import com.emma.Ecommerce.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AddressRepository addressRepository;

    /*
    This Api gets the address of a particular user when he is logged in to our system.
    https://localhost/8080/user/1/address. The userId changes from one user to another.
    */
    @GetMapping("/{userId}/address")
    public ResponseEntity<List<Address>> getAddress(@AuthenticationPrincipal LocalUser user,
                                                        @PathVariable Long userId) {

        if (!userHasPermission(user, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(addressRepository.findByUser_id(userId));
    }

    /* This method checks user permission and sets the address id to null in case someone wants
     to hack our system and changes someone address, then we create a dummy user and set the id
     to the user, then we sign the dummy user to the address and saves to the database. This adds
     to the address we have, and it returns with a new address id. NB: It only adds address
     associated to a particular user, a user can have two addresses. In this Api, the json input
     parameters are, addressLine1, addressLine2, city, country and addressId(optional).
     https://localhost/8080/1/address. The userId changes from one user to another.
     */
    @PutMapping("/{userId}/address")
    public ResponseEntity<Address> putAddress(@AuthenticationPrincipal LocalUser user,
                                              @PathVariable Long userId,
                                              @RequestBody Address address) {

        if (!userHasPermission(user, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        address.setId(null);
        LocalUser refUser = new LocalUser();
        refUser.setId(userId);
        address.setUser(refUser);
        return ResponseEntity.ok(addressRepository.save(address));
    }

    /*
    This method patches the existing address, if you want to edit the existing address, this Api
    allows us to perform the task. Parameters needed in json format includes, addressLine1,
    addressLine2, city and country and the addressId(must). The userId and the addressId changes
    from one user and one address to another. https://localhost/8080/2/address/4.
    */
    @PatchMapping("/{userId}/address/{addressId}")
    public ResponseEntity<Address> patchAddress(@AuthenticationPrincipal LocalUser user,
                                                @PathVariable Long userId,
                                                @PathVariable Long addressId,
                                                @RequestBody Address address) {

        if (!userHasPermission(user, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (address.getId().equals(addressId)) {
            //address.getId() == addressId
            Optional<Address> optionalAddress = addressRepository.findById(addressId);

            if (optionalAddress.isPresent()) {
                LocalUser originalUser = optionalAddress.get().getUser();

                if (originalUser.getId().equals(userId)) {
                    //originalUser.getId() == userId
                    address.setUser(originalUser);
                    return ResponseEntity.ok(addressRepository.save(address));
                }
            }
        }
        return ResponseEntity.badRequest().build();
    }

    /*This method ensures that you can only access a particular user id address only when
    you are log in as the user, that is using jwt token.
    */
    private boolean userHasPermission(LocalUser user, Long id) {
        /* return user.getId() == id; */
        return Objects.equals(user.getId(), id);
    }
}
