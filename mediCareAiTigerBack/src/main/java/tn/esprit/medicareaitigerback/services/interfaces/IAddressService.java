package tn.esprit.medicareaitigerback.services.interfaces;


import tn.esprit.medicareaitigerback.exceptions.AddressDontMatchWithUserException;
import tn.esprit.medicareaitigerback.exceptions.AddressNotFoundException;
import tn.esprit.medicareaitigerback.exceptions.PatientNotFoundException;
import tn.esprit.medicareaitigerback.models.dto.request.AddressAddDTO;
import tn.esprit.medicareaitigerback.models.dto.request.AddressUpdDTO;
import tn.esprit.medicareaitigerback.models.dto.response.AddressDTO;

import java.util.List;

public interface IAddressService {

    AddressDTO add(AddressAddDTO dto);

    void update(Long addressId, AddressUpdDTO dto) throws AddressNotFoundException, AddressDontMatchWithUserException;

    void delete(Long addressId) throws  AddressNotFoundException, AddressDontMatchWithUserException;

    List<AddressDTO> findAllByClientId(Long clientId) throws PatientNotFoundException;
}
