package com.mmwwtt.demo.common.vo;

import com.mmwwtt.demo.common.converter.ContactConverter;
import com.mmwwtt.demo.common.entity.Contact;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactVO {
    private Long contactId;
    private String email;
    private String phoneNumber;
    private String address;

    public static ContactVO getInstance() {
        return ContactConverter.INSTANCE.converterToVO(Contact.getInstance());
    }
}
