namespace CultureXAPI.DTOs
{
    public class UpdateUserSettingsDTO
    {

        public string PreferredLanguage { get; set; } = "en";
        public bool BiometricEnabled { get; set; }
        public object NotificationPreferences { get; set; }

    }
}
