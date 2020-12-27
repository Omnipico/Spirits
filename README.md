# Spirits

Spirits is a plugin meant for roleplay wherein dead players become semi-transparent ghosts.

Note: Plugin requires ProtocolLib

Note: Requires two teams, one for humans and one for spirits, the following commands will set that up.

    /team add Humans
    /team add Spirits
    /team join Humans %Humans
    /team join Spirits %Spirits
    /team modify Humans seeFriendlyInvisibles false

Commands:

    /spirits help -- Lists the Spirits commands
    /spirits setlives <player> <amount> -- Sets <player>'s non-canon life count to <amount>
    /spirits revive [player] -- Makes <player> no longer a spirit
    /spirits kill [player] -- Makes <player> a spirit
    /spirits hardcore [player] -- Toggles "hardcore", setting their lives to 1 (useful for RP)

Permissions:

    spirits.spirits -- allow usage of /spirits
    spirits.setlives -- allow setting lives
    spirits.revive -- allow reviving self
    spirits.revive.others -- allow reviving others
    spirits.kill -- allow killing self
    spirits.kill.others -- allow killing others
    spirits.hardcore -- allow setting self to 1 life
    spirits.hardcore.others -- allow setting others to 1 life
    spirits.* -- all permissions    