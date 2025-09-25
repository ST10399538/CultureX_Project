namespace CultureXAPI.DTOs
{
    public class AuthResponseDTO
    {

        public string AccessToken { get; set; }
        public string RefreshToken { get; set; }
        public UserProfileDTO User { get; set; }

    }
}
