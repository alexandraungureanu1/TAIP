import React, { useState } from 'react';

const MyForm = () => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    countryCode: '',
    email: '',
    personalIdentification: '',
    documentIdentification: '',
    encodedDocument: null,
  });

  const [responseMessage, setResponseMessage] = useState('');

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    setFormData({
      ...formData,
      encodedDocument: file,
    });
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();

    const formDataWithBase64 = {
      ...formData,
      encodedDocument: await convertImageToBase64(formData.encodedDocument),
    };

    try {
      const response = await fetch('http://localhost:8081/verify/nationality', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'API_KEY eyJzaWduYXR1cmUiOiI5ZTBkNjQ2ZmM4ZjQ1MzM3ODRhMTY5MDBmZTRkNTA3ODI5ZWUzY2NiZjljNmI3YzlkNzc5M2MyNmFlMWNkZTViIiwiaXNzdWVEYXRlIjpbMjAyMywxMiw1XSwiZXhwaXJ5RGF0ZSI6WzIwMjMsMTIsNl0sImNsaWVudElkIjoic3BvdGlmeSIsImlzc3VlciI6Im1lIiwicm9sZXMiOlsiUk9MRV9CQVNJQyJdfQ=='
        },
        body: JSON.stringify(formDataWithBase64),
      });

      const result = await response.text()
      console.log(result);
      setResponseMessage((result === 'true') ? 'Data corresponds' : 'Data and document don\'t match');
    } catch (error) {
      console.error('Error sending request:', error);
      setResponseMessage('An error occurred while processing your request.');
    }
  };

  const convertImageToBase64 = (imageFile) => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onloadend = () => {
        resolve("data:image/jpeg;base64," + reader.result.split(',')[1]);
      };
      reader.onerror = reject;
      reader.readAsDataURL(imageFile);
    });
  };

  return (
    <div>
      <form onSubmit={handleFormSubmit}>
        <label>
          First Name:
          <input type="text" name="firstName" value={formData.firstName} onChange={handleInputChange} required /> <br/>
        </label>
        <label>
          Last Name:
          <input type="text" name="lastName" value={formData.lastName} onChange={handleInputChange} required /><br/>
        </label>
        <label>
          Country Code:
          <input type="text" name="countryCode" value={formData.countryCode} onChange={handleInputChange} required /><br/>
        </label>
        <label>
          Email:
          <input type="email" name="email" value={formData.email} onChange={handleInputChange} required /><br/>
        </label>
        <label>
          Personal Identification:
          <input type="text" name="personalIdentification" value={formData.personalIdentification} onChange={handleInputChange} required /><br/>
        </label>
        <label>
          Document Identification:
          <input type="text" name="documentIdentification" value={formData.documentIdentification} onChange={handleInputChange} required /><br/>
        </label>
        <label>
          Image:
          <input type="file" accept="image/*" onChange={handleImageChange} required /><br/>
        </label>
        <button type="submit">Submit</button><br/>
      </form>
      <div>{responseMessage}</div>
    </div>
  );
};

export default MyForm;
